package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.request.AuthenticationRequest;
import com.dhbw.kinoticket.request.RegisterRequest;
import com.dhbw.kinoticket.response.AuthenticationResponse;
import com.dhbw.kinoticket.service.AuthenticationService;
import com.dhbw.kinoticket.service.LogoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {AuthenticationControllerTest.class})
public class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LogoutService logoutService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }


    @Test
    @Order(1)
    public void test_Register() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authenticationResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    public void test_Authenticate() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(authenticationResponse);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authenticationRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }


    // Helper method to convert object to JSON string
    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}