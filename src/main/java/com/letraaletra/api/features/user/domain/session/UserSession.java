package com.letraaletra.api.features.user.domain.session;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSession {
    private final UUID id;
    private final UUID userId;
    private String currentTokenHash;
    private String previousTokenHash;
    private LocalDateTime previousTokenRotatedAt;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private SessionRevocationReason revocationReason;

    private UserSession(
            UUID id,
            UUID userId,
            String currentTokenHash,
            String previousTokenHash,
            LocalDateTime previousTokenRotatedAt,
            LocalDateTime createdAt,
            LocalDateTime lastUsedAt,
            LocalDateTime expiresAt,
            LocalDateTime revokedAt,
            SessionRevocationReason revocationReason
    ) {
        this.id = id;
        this.userId = userId;
        this.currentTokenHash = currentTokenHash;
        this.previousTokenHash = previousTokenHash;
        this.previousTokenRotatedAt = previousTokenRotatedAt;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.revocationReason = revocationReason;
    }

    public static UserSession create(
            UUID userId,
            String currentTokenHash,
            LocalDateTime now,
            LocalDateTime expiresAt
    ) {
        return new UserSession(
                UUID.randomUUID(),
                userId,
                currentTokenHash,
                null,
                null,
                now,
                now,
                expiresAt,
                null,
                null
        );
    }

    public static UserSession restore(
            UUID id,
            UUID userId,
            String currentTokenHash,
            String previousTokenHash,
            LocalDateTime previousTokenRotatedAt,
            LocalDateTime createdAt,
            LocalDateTime lastUsedAt,
            LocalDateTime expiresAt,
            LocalDateTime revokedAt,
            SessionRevocationReason revocationReason
    ) {
        return new UserSession(
                id,
                userId,
                currentTokenHash,
                previousTokenHash,
                previousTokenRotatedAt,
                createdAt,
                lastUsedAt,
                expiresAt,
                revokedAt,
                revocationReason
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCurrentTokenHash() {
        return currentTokenHash;
    }

    public String getPreviousTokenHash() {
        return previousTokenHash;
    }

    public LocalDateTime getPreviousTokenRotatedAt() {
        return previousTokenRotatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public SessionRevocationReason getRevocationReason() {
        return revocationReason;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public boolean matchesCurrent(String tokenHash) {
        return currentTokenHash != null && currentTokenHash.equals(tokenHash);
    }

    public boolean matchesPrevious(String tokenHash) {
        return previousTokenHash != null && previousTokenHash.equals(tokenHash);
    }

    public boolean isPreviousWithinRecoveryWindow(LocalDateTime now, long recoveryWindowMillis) {
        return previousTokenHash != null
                && previousTokenRotatedAt != null
                && !now.isAfter(previousTokenRotatedAt.plusNanos(recoveryWindowMillis * 1_000_000L));
    }

    public void rotate(String newTokenHash, LocalDateTime now, LocalDateTime newExpiresAt) {
        this.previousTokenHash = this.currentTokenHash;
        this.previousTokenRotatedAt = now;
        this.currentTokenHash = newTokenHash;
        this.lastUsedAt = now;
        this.expiresAt = newExpiresAt;
    }

    public void renew(String newTokenHash, LocalDateTime now, LocalDateTime newExpiresAt) {
        this.previousTokenHash = null;
        this.previousTokenRotatedAt = null;
        this.currentTokenHash = newTokenHash;
        this.lastUsedAt = now;
        this.expiresAt = newExpiresAt;
        this.revokedAt = null;
        this.revocationReason = null;
    }

    public void revoke(SessionRevocationReason reason, LocalDateTime now) {
        if (isRevoked()) {
            return;
        }

        this.revokedAt = now;
        this.revocationReason = reason;
    }
}
