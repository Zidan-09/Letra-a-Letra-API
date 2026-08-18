package com.letraaletra.api.features.admin.application.port;


public interface AdminInvitationEmailService {
    void send(String email, String recipient, String token);
}
