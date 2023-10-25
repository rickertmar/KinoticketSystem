package com.dhbw.kinoticket.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.request.EmailDetails;
import com.dhbw.kinoticket.response.MovieResponse;
import com.dhbw.kinoticket.response.ReservationResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {EmailSenderServiceTest.class})
public class EmailSenderServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailClient emailClient;

    @Mock
    private ReservationResponse reservationResponse;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        emailSenderService = new EmailSenderService(emailClient);
    }

/*
    @Test
    @Disabled
    void testSendHtmlEmailWhenInputIsValidThenReturnResult() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient("test@example.com");
        emailDetails.setSubject("Test Subject");
        emailDetails.setBody("Test Body");

        EmailMessage message = new EmailMessage()
                .setSenderAddress("<donotreply@0338e80f-c059-4474-8293-a3734f367eae.azurecomm.net>")
                .setToRecipients(emailDetails.getRecipient())
                .setSubject(emailDetails.getSubject())
                .setBodyHtml(emailDetails.getBody());

        EmailSendResult emailSendResult = new EmailSendResult(null, null, null);

        // Use reflection to set the values
        Field statusField = emailSendResult.getClass().getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(emailSendResult, EmailSendStatus.SUCCEEDED);

        Field idField = emailSendResult.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(emailSendResult, "testId");

        SyncPoller<EmailSendResult, EmailSendResult> poller = mock(SyncPoller.class);
        when(poller.getFinalResult()).thenReturn(emailSendResult);

        // Mock the emailClient and return the poller
        EmailClient emailClient = mock(EmailClient.class);
        when(emailClient.beginSend(message, null)).thenReturn(poller);

        // Create an instance of the EmailSenderService and pass the mocked emailClient
        EmailSenderService emailSenderService = new EmailSenderService(emailClient);

        // Act
        String result = emailSenderService.sendHtmlEmail(emailDetails);

        // Assert
        assertEquals("Successfully sent the email (operation id: %stestId)", result);
    }

    @Test
    @Disabled
    void testSendHtmlEmail_Failure() throws Exception {
        // Create a sample EmailDetails
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient("recipient@example.com");
        emailDetails.setSubject("Test Subject");
        emailDetails.setBody("Test Body");

        // Mock the behavior of EmailClient for a failure scenario
        EmailSendResult emailSendResult = new EmailSendResult();
        emailSendResult.setStatus(EmailSendStatus.FAILED);
        emailSendResult.setError(new EmailSendError("Error Message"));


        // Call the method under test
        String result = emailSenderService.sendHtmlEmail(emailDetails);

        // Assertions
        String expected = "Error sending the email: " + emailSendResult.getError().getMessage();
        Assertions.assertEquals(expected, result);
    }
*/
    @Test
    @Order(1)
    void test_GenerateReservationEmailBodyPlainText() {
        // Arrange
        when(reservationResponse.getMovie()).thenReturn(MovieResponse.builder()
                .title("Movie Title")
                .fsk(FSK.FSK12)
                .runtime("120")
                .description("Movie Description")
                .build());
        when(reservationResponse.getTime()).thenReturn(LocalDateTime.of(2023, 10, 2, 15, 30));
        when(reservationResponse.getTickets()).thenReturn(Arrays.asList(
                Ticket.builder().seat(Seat.builder().seatRow('A').number(1).build()).discount(Discount.STUDENT).isValid(true).build(),
                Ticket.builder().seat(Seat.builder().seatRow('B').number(2).build()).discount(Discount.CHILD).isValid(true).build()
        ));
        when(reservationResponse.getTotal()).thenReturn(15.0);

        // Act
        String emailBody = emailSenderService.generateReservationEmailBodyPlainText(reservationResponse);

        // Assert
        String expectedEmailBody = "Reservation Confirmation\n\n" +
                "Movie Details\n" +
                "Title: Movie Title\n" +
                "FSK Rating: FSK12\n" +
                "Runtime: 120\n" +
                "Description: Movie Description\n" +
                "\nShowtime Details\n" +
                "Date: 2023-10-02\n" +
                "Time: 15:30:00\n" +
                "\nTicket Details\n" +
                "Seat Row: A\n" +
                "Seat Number: 1\n" +
                "Discount: STUDENT\n" +
                "Validity: Valid\n" +
                "Seat Row: B\n" +
                "Seat Number: 2\n" +
                "Discount: CHILD\n" +
                "Validity: Valid\n" +
                "\nTotal Amount\n" +
                "Total: €15.0\n" +
                "\nDHBWKino Website:\n" +
                "https://dhbwkino.de/\n";

        assertEquals(expectedEmailBody, emailBody);
    }

    @Test
    @Order(2)
    void test_GenerateReservationEmailBodyHTML() {
        // Arrange
        when(reservationResponse.getMovie()).thenReturn(MovieResponse.builder()
                .title("Movie Title")
                .fsk(FSK.FSK12)
                .runtime("120")
                .description("Movie Description")
                .build());
        when(reservationResponse.getTime()).thenReturn(LocalDateTime.of(2023, 10, 2, 15, 30));
        when(reservationResponse.getTickets()).thenReturn(Arrays.asList(
                Ticket.builder().seat(Seat.builder().seatRow('A').number(1).build()).discount(Discount.STUDENT).isValid(true).build(),
                Ticket.builder().seat(Seat.builder().seatRow('B').number(2).build()).discount(Discount.CHILD).isValid(true).build()
        ));
        when(reservationResponse.getTotal()).thenReturn(15.0);

        // Act
        String emailBody = emailSenderService.generateReservationEmailBodyHTML(reservationResponse);

        // Assert
        String expectedEmailBody = "<html><body>" +
                "<h2>Reservation Confirmation</h2>" +
                "<h3>Movie Details</h3>" +
                "<p><strong>Title:</strong> Movie Title</p>" +
                "<p><strong>FSK Rating:</strong> FSK12</p>" +
                "<p><strong>Runtime:</strong> 120</p>" +
                "<p><strong>Description:</strong> Movie Description</p>" +
                "<h3>Showtime Details</h3>" +
                "<p><strong>Date:</strong> 2023-10-02</p>" +
                "<p><strong>Time:</strong> 15:30:00</p>" +
                "<h3>Ticket Details</h3>" +
                "<p><strong>Seat Row:</strong> A</p>" +
                "<p><strong>Seat Number:</strong> 1</p>" +
                "<p><strong>Discount:</strong> STUDENT</p>" +
                "<p><strong>Validity:</strong> Valid</p>" +
                "<p><strong>Seat Row:</strong> B</p>" +
                "<p><strong>Seat Number:</strong> 2</p>" +
                "<p><strong>Discount:</strong> CHILD</p>" +
                "<p><strong>Validity:</strong> Valid</p>" +
                "<h3>Total Amount</h3>" +
                "<p><strong>Total:</strong> €15.0</p>" +
                "<h3>DHBWKino Website</h3>" +
                "<p><a href='https://dhbwkino.de/'>https://dhbwkino.de/</a></p>" +
                "</body></html>";

        assertEquals(expectedEmailBody, emailBody);
    }
}