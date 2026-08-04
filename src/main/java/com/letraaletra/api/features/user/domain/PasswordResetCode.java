package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.user.domain.exception.MaxAttemptsExceededException;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetCode {
    private final UUID id;
    private final UUID userId;
    private final String codeHash;
    private boolean used;
    private int attempts;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    private PasswordResetCode(
            UUID id,
            UUID userId,
            String codeHash,
            boolean used,
            int attempts,
            LocalDateTime createdAt,
            LocalDateTime expiresAt

    ) {
        this.id = id;
        this.userId = userId;
        this.codeHash = codeHash;
        this.used = used;
        this.attempts = attempts;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static PasswordResetCode create(
            UUID userId,
            String codeHash
    ) {
        return new PasswordResetCode(
                UUID.randomUUID(),
                userId,
                codeHash,
                false,
                0,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );
    }

    public static PasswordResetCode restore(
            UUID id,
            UUID userId,
            String codeHash,
            boolean used,
            int attempts,
            LocalDateTime createdAt,
            LocalDateTime expiresAt
    ) {
        return new PasswordResetCode(
                id,
                userId,
                codeHash,
                used,
                attempts,
                createdAt,
                expiresAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCodeHash() {
        return codeHash;
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

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        if (attempts >= 5) {
            throw new MaxAttemptsExceededException();
        }

        attempts++;
    }

    public void markAsUsed() {
        used = true;
    }

    public void validate(String code, TokenHashService tokenHashService) {
        if (used) {
            throw new InvalidTokenException();
        }

        if (isExpired()) {
            throw new InvalidTokenException();
        }

        if (!tokenHashService.matches(code, codeHash)) {
            incrementAttempts();
            throw new InvalidTokenException();
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
