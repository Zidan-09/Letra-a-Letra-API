package com.letraaletra.api.features.admin.domain;

import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminPasswordSetupToken {
    private final String tokenHash;
    private final UUID adminId;
    private final LocalDateTime expiresAt;
    private boolean used;

    private AdminPasswordSetupToken(
            String tokenHash,
            UUID adminId,
            LocalDateTime expiresAt,
            boolean used
    ) {
        this.tokenHash = tokenHash;
        this.adminId = adminId;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public static AdminPasswordSetupToken create(
            String tokenHash,
            UUID adminId,
            LocalDateTime expiresAt
    ) {
        return new AdminPasswordSetupToken(
                tokenHash,
                adminId,
                expiresAt,
                false
        );
    }

    public static AdminPasswordSetupToken restore(
            String tokenHash,
            UUID adminId,
            LocalDateTime expiresAt,
            boolean used
    ) {
        return new AdminPasswordSetupToken(
                tokenHash,
                adminId,
                expiresAt,
                used
        );
    }

    public UUID getAdminId() {
        return adminId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void markAsUsed() {
        used = true;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void validate() {
        if (used) {
            throw new InvalidTokenException();
        }

        if (isExpired()) {
            throw new InvalidTokenException();
        }
    }
}
