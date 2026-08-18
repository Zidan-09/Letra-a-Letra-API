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
@Table(name = "\"password_reset_code\"")
public class PasswordResetCodeJpaEntity {
    @Id
    @Column(name = "password_reset_code_id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "code_hash")
    private String codeHash;

    @Column(name = "used")
    private boolean used;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
