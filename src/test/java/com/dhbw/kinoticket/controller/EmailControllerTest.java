package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.request.EmailDetails;
import com.dhbw.kinoticket.service.EmailSenderService;
import com.dhbw.kinoticket.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {EmailControllerTest.class})
class EmailControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
    }

    @Test
    @Order(1)
    public void test_SendMail_Success() throws Exception {

        // Arrange
        EmailDetails emailDetails = new EmailDetails(null, "test", "test", null);

        // Mock
        when(emailService.sendSimpleMail(emailDetails))
                .thenReturn("Mail sent successfully...");

        // Act and Assert
        mockMvc.perform(post("/mail/sendMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDetails)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    public void test_SendMail_Failure() throws Exception {

        // Arrange
        EmailDetails emailDetails = null;

        // Mock
        when(emailService.sendSimpleMail(emailDetails))
                .thenThrow(new RuntimeException("Mail sending failed"));

        // Act and Assert
        mockMvc.perform(post("/mail/sendMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDetails)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @Order(3)
    public void test_SendHtmlMail_Success() throws Exception {

        // Arrange
        EmailDetails emailDetails = new EmailDetails(null, "test", "test", null);

        // Mock
        when(emailService.sendHtmlMail(emailDetails))
                .thenReturn("Mail sent successfully...");

        // Act and Assert
        mockMvc.perform(post("/mail/sendHtmlMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDetails)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(4)
    public void test_SendHtmlMail_Failure() throws Exception {

        // Arrange
        EmailDetails emailDetails = null;

        // Mock
        when(emailService.sendHtmlMail(emailDetails))
                .thenThrow(new RuntimeException("Mail sending failed"));

        // Act and Assert
        mockMvc.perform(post("/mail/sendHtmlMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDetails)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @Order(5)
    public void test_SendMailWithAttachments_Success() throws Exception {

        // Arrange
        EmailDetails emailDetails = new EmailDetails(null, "test", "test", null);

        // Mock
        when(emailService.sendMailWithAttachments(emailDetails))
                .thenReturn("Mail sent successfully...");

        // Act and Assert
        mockMvc.perform(post("/mail/sendMailWithAttachment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDetails)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(6)
    public void test_SendMailWithAttachments_Failure() throws Exception {

        // Arrange
        EmailDetails emailDetails = null;

        // Mock
        when(emailService.sendMailWithAttachments(emailDetails))
                .thenThrow(new RuntimeException("Mail sending failed"));

        // Act and Assert
        mockMvc.perform(post("/mail/sendMailWithAttachment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDetails)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}