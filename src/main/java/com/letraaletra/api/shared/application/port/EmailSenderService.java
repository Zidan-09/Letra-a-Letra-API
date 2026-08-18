package com.letraaletra.api.shared.application.port;

public interface EmailSenderService {
    void send(String recipient, String subject, String body);
}
