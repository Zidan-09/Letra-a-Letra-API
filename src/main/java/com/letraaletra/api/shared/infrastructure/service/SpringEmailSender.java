package com.letraaletra.api.shared.infrastructure.service;

import com.letraaletra.api.shared.application.port.EmailSenderService;
import com.letraaletra.api.shared.domain.exception.EmailSendException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class SpringEmailSender implements EmailSenderService {
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(SpringEmailSender.class);

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void send(String recipient, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

        } catch (Exception e) {
            logger.error("Error to send email to: {}; Error:", recipient, e);
            throw new EmailSendException();
        }
    }
}
