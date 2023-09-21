package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {JwtServiceTest.class})
public class JwtServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService(tokenRepository);
        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    @Order(1)
    public void test_BuildToken_WhenGivenValidInputs() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("claim1", "value1");

        // Act
        String token = jwtService.buildToken(extraClaims, userDetails, 1000);

        // Assert
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtService.getSignInKey()).build().parseClaimsJws(token).getBody();
        assertThat(claims.getSubject()).isEqualTo(userDetails.getUsername());
    }

    @Test
    @Order(2)
    public void test_BuildToken_WhenGivenEmptyMap() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();

        // Act
        String token = jwtService.buildToken(extraClaims, userDetails, 1000);

        // Assert
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtService.getSignInKey()).build().parseClaimsJws(token).getBody();
        assertThat(claims.getSubject()).isEqualTo(userDetails.getUsername());
    }
}