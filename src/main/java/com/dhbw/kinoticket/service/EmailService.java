package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.request.EmailDetails;

public interface EmailService {

    String sendSimpleMail(EmailDetails details);

    String sendHtmlMailMimeMessage(EmailDetails details);

    String sendMailWithAttachments(EmailDetails details);

}
