package com.dhbw.kinoticket.service;

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
    private ReservationResponse reservationResponse;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        emailSenderService = new EmailSenderService();
    }


    @Test
    @Order(1)
    void testGenerateReservationEmailBodyPlainText() {
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