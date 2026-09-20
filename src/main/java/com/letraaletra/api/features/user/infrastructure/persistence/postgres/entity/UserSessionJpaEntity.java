package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"user_session\"")
public class UserSessionJpaEntity {
    @Id
    @Column(name = "user_session_id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "current_token_hash")
    private String currentTokenHash;

    @Column(name = "previous_token_hash")
    private String previousTokenHash;

    @Column(name = "previous_token_rotated_at")
    private LocalDateTime previousTokenRotatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revocation_reason")
    private String revocationReason;
}
