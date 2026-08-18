package com.letraaletra.api.features.admin.application.port;

public interface PasswordResetTokenEmailService {
    void send(String email, String recipient, String token);
}
