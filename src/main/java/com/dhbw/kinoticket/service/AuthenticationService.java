package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Role;
import com.dhbw.kinoticket.entity.Token;
import com.dhbw.kinoticket.entity.TokenType;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.TokenRepository;
import com.dhbw.kinoticket.repository.UserRepository;
import com.dhbw.kinoticket.request.AuthenticationRequest;
import com.dhbw.kinoticket.request.RegisterRequest;
import com.dhbw.kinoticket.response.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.event.AccessWatchpointEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    //Register form
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) //Encoding via passwordEncoder -> BCrpyt
                .role(Role.USER)
                .build();
        var savedUser = userRepository.save(user); //saving into database
        Map<String, Object> userClaims = new TreeMap<String, Object>();
        userClaims.put("ROLE", user.getRole());
        var jwtToken = jwtService.generateToken(userClaims, user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build(); //returning Auth Token
    }
    //Login form
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())); //auth of email and password
        var user =  userRepository.findByEmail(request.getEmail()).orElseThrow();
        Map<String, Object> userClaims = new TreeMap<String, Object>();
        userClaims.put("ROLE", user.getRole());
        var jwtToken = jwtService.generateToken(userClaims, user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build(); //returning Auth Token
    }

    void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    void revokeAllUserToken(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(token ->{
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if(authHeader!= null && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
            userEmail = jwtService.extractUsername(refreshToken);
            if(userEmail != null) {
                var user = this.userRepository.findByEmail(userEmail).orElseThrow();
                Map<String, Object> userClaims = new TreeMap<String, Object>();
                userClaims.put("ROLE", user.getRole());
                if(jwtService.isTokenValid(refreshToken, user)){
                    var accessToken = jwtService.generateToken(userClaims, user);
                    revokeAllUserToken(user);
                    saveUserToken(user, accessToken);
                    var authResponse = AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            }
        }
    }

}
