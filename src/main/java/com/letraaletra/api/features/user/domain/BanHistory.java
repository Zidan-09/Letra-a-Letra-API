package com.letraaletra.api.features.user.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class BanHistory {
    private final UUID id;
    private final UUID userId;
    private final UUID adminId;
    private final String reason;
    private final BanType type;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private LocalDateTime removedAt;
    private UUID removedBy;

    private BanHistory(
            UUID id,
            UUID userId,
            UUID adminId,
            String reason,
            BanType type,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime removedAt,
            UUID removedBy
    ) {
        this.id = id;
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.type = type;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.removedAt = removedAt;
        this.removedBy = removedBy;
    }

    public static BanHistory create(
            UUID userId,
            UUID adminId,
            String reason,
            BanType type,
            int expiresAt
    ) {
        return new BanHistory(
               UUID.randomUUID(),
                userId,
                adminId,
                reason,
                type,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(expiresAt),
                null,
                null
        );
    }

    public static BanHistory restore(
            UUID id,
            UUID userId,
            UUID adminId,
            String reason,
            BanType type,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime removedAt,
            UUID removedBy
    ) {
        return new BanHistory(
                id,
                userId,
                adminId,
                reason,
                type,
                createdAt,
                expiresAt,
                removedAt,
                removedBy
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public BanType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getRemovedAt() {
        return removedAt;
    }

    public UUID getRemovedBy() {
        return removedBy;
    }

    public void removeBan(UUID adminId) {
        removedBy = adminId;
        removedAt = LocalDateTime.now();
    }
}
