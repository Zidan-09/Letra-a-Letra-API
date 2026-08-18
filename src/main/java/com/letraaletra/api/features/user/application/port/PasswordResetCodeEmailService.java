package com.letraaletra.api.features.user.application.port;

public interface PasswordResetCodeEmailService {
    void send(String email, String recipient, String code);
}
