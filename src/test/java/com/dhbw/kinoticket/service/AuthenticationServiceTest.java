package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Token;
import com.dhbw.kinoticket.entity.TokenType;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.TokenRepository;
import com.dhbw.kinoticket.repository.UserRepository;
import com.dhbw.kinoticket.request.AuthenticationRequest;
import com.dhbw.kinoticket.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {AuthenticationServiceTest.class})
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User user;
    private String jwtToken;

    @BeforeEach
    public void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("john.doe@example.com");
        authenticationRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        jwtToken = "jwtToken";
    }

    @Test
    @Order(1)
    public void test_Register_WhenValidRequest_ThenReturnsAuthenticationResponse() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        var response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(jwtService, times(1)).generateToken(anyMap(), any(User.class));
        verify(jwtService, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    @Order(2)
    public void test_Authenticate_WhenGivenValidInput_ThenReturnAuthenticationResponse() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        when(userRepository.findByEmail(eq(authenticationRequest.getEmail()))).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        var response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(eq(authenticationRequest.getEmail())); // Use eq() to match the email value
        verify(jwtService, times(1)).generateToken(anyMap(), any(User.class));
        verify(jwtService, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    @Order(3)
    public void test_SaveUserToken_WhenCalled_ThenTokenSaved() {
        // Arrange
        Token expectedToken = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        // Act
        authenticationService.saveUserToken(user, jwtToken);

        // Assert
        verify(tokenRepository, times(1)).save(expectedToken);
    }

    @Test
    @Order(4)
    public void test_SaveUserToken_WhenCalled_ThenTokenPropertiesSet() {
        // Arrange
        Token expectedToken = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        // Act
        authenticationService.saveUserToken(user, jwtToken);

        // Assert
        verify(tokenRepository, times(1)).save(argThat(token -> {
            assertEquals(expectedToken.getUser(), token.getUser());
            assertEquals(expectedToken.getToken(), token.getToken());
            assertEquals(expectedToken.getTokenType(), token.getTokenType());
            assertEquals(expectedToken.isExpired(), token.isExpired());
            assertEquals(expectedToken.isRevoked(), token.isRevoked());
            return true;
        }));
    }

    @Test
    @Order(5)
    public void test_RevokeAllUserToken_WhenValidTokensExist_ThenTokensAreRevoked() {
        // Arrange
        Token token1 = new Token();
        Token token2 = new Token();
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Arrays.asList(token1, token2));

        // Act
        authenticationService.revokeAllUserToken(user);

        // Assert
        assertTrue(token1.isExpired());
        assertTrue(token1.isRevoked());
        assertTrue(token2.isExpired());
        assertTrue(token2.isRevoked());
        verify(tokenRepository, times(1)).saveAll(Arrays.asList(token1, token2));
    }

    @Test
    @Order(6)
    public void test_RevokeAllUserToken_WhenNoValidTokens_ThenNoTokensAreRevoked() {
        // Arrange
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Arrays.asList());

        // Act
        authenticationService.revokeAllUserToken(user);

        // Assert
        verify(tokenRepository, never()).saveAll(any());
    }

    @Test
    @Order(7)
    public void test_RefreshToken_WhenAuthorizationHeaderIsNull() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).extractUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @Order(8)
    public void test_RefreshToken_WhenAuthorizationHeaderDoesNotStartWithBearer() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("NotBearer token");

        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).extractUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @Order(9)
    public void test_RefreshToken_WhenUserEmailIsNull() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(jwtService.extractUsername(anyString())).thenReturn(null);

        authenticationService.refreshToken(request, response);

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @Order(10)
    public void test_RefreshToken_WhenUserDoesNotExist() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(jwtService.extractUsername(anyString())).thenReturn("email@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.refreshToken(request, response));
    }

    @Test
    @Order(11)
    public void test_RefreshToken_WhenTokenIsNotValid() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(jwtService.extractUsername(anyString())).thenReturn("email@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(false);

        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).generateToken(anyMap(), any(User.class));
    }
}