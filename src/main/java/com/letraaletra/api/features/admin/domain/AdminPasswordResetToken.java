package com.letraaletra.api.features.admin.domain;

import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminPasswordResetToken {
    private final UUID id;
    private final UUID adminId;
    private final String tokenHash;
    private boolean used;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    private AdminPasswordResetToken(
            UUID id,
            UUID adminId,
            String tokenHash,
            boolean used,
            LocalDateTime createdAt,
            LocalDateTime expiresAt
    ) {
        this.id = id;
        this.adminId = adminId;
        this.tokenHash = tokenHash;
        this.used = used;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static AdminPasswordResetToken create(
            UUID adminId,
            String tokenHash
    ) {
        return new AdminPasswordResetToken(
                UUID.randomUUID(),
                adminId,
                tokenHash,
                false,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );
    }

    public static AdminPasswordResetToken restore(
            UUID id,
            UUID adminId,
            String tokenHash,
            boolean used,
            LocalDateTime createdAt,
            LocalDateTime expiresAt
    ) {
        return new AdminPasswordResetToken(
                id,
                adminId,
                tokenHash,
                used,
                createdAt,
                expiresAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public boolean isUsed() {
        return used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    private boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsUsed() {
        used = true;
    }
    public void validate(String token, TokenHashService tokenHashService) {
        if (used) {
            throw new InvalidTokenException();
        }

        if (isExpired()) {
            throw new InvalidTokenException();
        }

        if (!tokenHashService.matches(token, tokenHash)) {
            throw new InvalidTokenException();
        }
    }

}
