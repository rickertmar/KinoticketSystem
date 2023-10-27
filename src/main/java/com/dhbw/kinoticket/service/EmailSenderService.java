package com.dhbw.kinoticket.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.dhbw.kinoticket.entity.Ticket;
import com.dhbw.kinoticket.request.EmailDetails;
import com.dhbw.kinoticket.response.ReservationResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
@Service
@RequiredArgsConstructor
public class EmailSenderService implements EmailService {

    private JavaMailSender javaMailSender;

    @Autowired
    private final EmailClient emailClient;

    @Value("${azure.communication.services.connection-string}")
    private String connectionString;

    @Value("spring.mail.username")
    private String sender;

    public String sendHtmlEmail(EmailDetails emailDetails) {
        EmailMessage message = new EmailMessage()
                .setSenderAddress("<donotreply@0338e80f-c059-4474-8293-a3734f367eae.azurecomm.net>")
                .setToRecipients(emailDetails.getRecipient())
                .setSubject(emailDetails.getSubject())
                .setBodyHtml(emailDetails.getBody());

        try
        {
            SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message, null);

            PollResponse<EmailSendResult> pollResponse = null;

            Duration timeElapsed = Duration.ofSeconds(0);
            Duration POLLER_WAIT_TIME = Duration.ofSeconds(10);

            while (pollResponse == null
                    || pollResponse.getStatus() == LongRunningOperationStatus.NOT_STARTED
                    || pollResponse.getStatus() == LongRunningOperationStatus.IN_PROGRESS)
            {
                pollResponse = poller.poll();
                System.out.println("Email send poller status: " + pollResponse.getStatus());

                Thread.sleep(POLLER_WAIT_TIME.toMillis());
                timeElapsed = timeElapsed.plus(POLLER_WAIT_TIME);

                if (timeElapsed.compareTo(POLLER_WAIT_TIME.multipliedBy(18)) >= 0)
                {
                    throw new RuntimeException("Polling timed out.");
                }
            }

            if (poller.getFinalResult().getStatus() == EmailSendStatus.SUCCEEDED)
            {
                System.out.printf("Successfully sent the email (operation id: %s)", poller.getFinalResult().getId());
                return "Successfully sent the email (operation id: %s)" + poller.getFinalResult().getId();
            }
            else
            {
                throw new RuntimeException(poller.getFinalResult().getError().getMessage());
            }
        }
        catch (Exception exception)
        {
            System.out.println(exception.getMessage());
            return "Error sending the email: " + exception.getMessage();
        }
    }


    // To send a simple email
    @Override
    public String sendSimpleMail(EmailDetails details) {
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail sent successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while sending mail";
        }
    }

    // To send an email with html content
    public String sendHtmlMailMimeMessage(EmailDetails details) {
        // Try block to check for exceptions
        try {
            // Create a MIME message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Setting up necessary details
            messageHelper.setFrom(sender);
            messageHelper.setTo(details.getRecipient());
            messageHelper.setSubject(details.getSubject());

            // Set the HTML content
            messageHelper.setText(details.getBody(), true);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent successfully...";
        } catch (MessagingException e) {
            return "Error while sending mail";
        }
    }

    // To send an email with attachment
    @Override
    public String sendMailWithAttachments(EmailDetails details) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to be sent
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getBody());
            mimeMessageHelper.setSubject(details.getSubject());

            // Adding the attachment
            FileSystemResource file = new FileSystemResource(
                    new File(details.getAttachment())
            );

            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent successfully...";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail";
        }
    }


    // Generate reservation confirmation message as plain text
    public String generateReservationEmailBodyPlainText(ReservationResponse reservation) {
        StringBuilder emailContent = new StringBuilder();

        // Add the email subject
        emailContent.append("Reservation Confirmation\n\n");

        // Add movie details
        emailContent.append("Movie Details\n");
        emailContent.append("Title: ").append(reservation.getMovie().getTitle()).append("\n");
        emailContent.append("FSK Rating: ").append(reservation.getMovie().getFsk()).append("\n");
        emailContent.append("Runtime: ").append(reservation.getMovie().getRuntime()).append("\n");
        emailContent.append("Description: ").append(reservation.getMovie().getDescription()).append("\n");

        // Add showing date and time
        emailContent.append("\nShowtime Details\n");
        emailContent.append("Date: ").append(reservation.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        emailContent.append("Time: ").append(reservation.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");

        // Add ticket details
        emailContent.append("\nTicket Details\n");
        for (Ticket ticket : reservation.getTickets()) {
            emailContent.append("Seat Row: ").append(ticket.getSeat().getSeatRow()).append("\n");
            emailContent.append("Seat Number: ").append(ticket.getSeat().getNumber()).append("\n");
            emailContent.append("Discount: ").append(ticket.getDiscount()).append("\n");
            emailContent.append("Validity: ").append(ticket.isValid() ? "Valid" : "Invalid").append("\n");
        }

        // Add total amount
        emailContent.append("\nTotal Amount\n");
        emailContent.append("Total: €").append(reservation.getTotal()).append("\n");

        // Add website URL as a footer
        emailContent.append("\nDHBWKino Website:\n");
        emailContent.append("https://dhbwkino.de/").append("\n");

        return emailContent.toString();
    }

    // Generate reservation confirmation message as html
    public String generateReservationEmailBodyHTML(ReservationResponse reservation) {
        StringBuilder emailContent = new StringBuilder();

        // Start HTML content
        emailContent.append("<html><body>");

        // Add the email subject
        emailContent.append("<h2>Reservation Confirmation</h2>");

        // Add movie details
        emailContent.append("<h3>Movie Details</h3>");
        emailContent.append("<p><strong>Title:</strong> ").append(reservation.getMovie().getTitle()).append("</p>");
        emailContent.append("<p><strong>FSK Rating:</strong> ").append(reservation.getMovie().getFsk()).append("</p>");
        emailContent.append("<p><strong>Runtime:</strong> ").append(reservation.getMovie().getRuntime()).append("</p>");
        emailContent.append("<p><strong>Description:</strong> ").append(reservation.getMovie().getDescription()).append("</p>");

        // Add showing date and time
        emailContent.append("<h3>Showtime Details</h3>");
        emailContent.append("<p><strong>Date:</strong> ").append(reservation.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("</p>");
        emailContent.append("<p><strong>Time:</strong> ").append(reservation.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("</p>");

        // Add ticket details
        emailContent.append("<h3>Ticket Details</h3>");
        for (Ticket ticket : reservation.getTickets()) {
            emailContent.append("<p><strong>Seat Row:</strong> ").append(ticket.getSeat().getSeatRow()).append("</p>");
            emailContent.append("<p><strong>Seat Number:</strong> ").append(ticket.getSeat().getNumber()).append("</p>");
            emailContent.append("<p><strong>Discount:</strong> ").append(ticket.getDiscount()).append("</p>");
            emailContent.append("<p><strong>Validity:</strong> ").append(ticket.isValid() ? "Valid" : "Invalid").append("</p>");
        }

        // Add total amount
        emailContent.append("<h3>Total Amount</h3>");
        emailContent.append("<p><strong>Total:</strong> €").append(reservation.getTotal()).append("</p>");

        // Add website URL as a clickable link
        emailContent.append("<h3>DHBWKino Website</h3>");
        emailContent.append("<p><a href='https://dhbwkino.de/'>https://dhbwkino.de/</a></p>");

        // End HTML content
        emailContent.append("</body></html>");

        return emailContent.toString();
    }


}
