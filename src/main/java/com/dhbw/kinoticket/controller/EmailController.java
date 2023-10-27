package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.request.EmailDetails;
import com.dhbw.kinoticket.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mail")
public class EmailController {

    @Autowired
    private EmailSenderService emailService;

    // Sending a simple Email
    @PostMapping("/sendMail")
    public ResponseEntity<?> sendMail(@RequestBody EmailDetails details) {
        try {
            return new ResponseEntity<>(emailService.sendSimpleMail(details), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Sending an Email with HTML content
    @PostMapping("/sendHtmlEmail")
    public ResponseEntity<?> sendHtmlEmail(@RequestBody EmailDetails details) {
        try {
            return new ResponseEntity<>(emailService.sendHtmlEmail(details), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Sending an Email with HTML content using MimeMessage
    @PostMapping("/sendHtmlMailMimeMessage")
    public ResponseEntity<?> sendHtmlMailMimeMessage(@RequestBody EmailDetails details) {
        try {
            return new ResponseEntity<>(emailService.sendHtmlMailMimeMessage(details), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public ResponseEntity<?> sendMailWithAttachment(@RequestBody EmailDetails details) {
        try {
            return new ResponseEntity<>(emailService.sendMailWithAttachments(details), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
