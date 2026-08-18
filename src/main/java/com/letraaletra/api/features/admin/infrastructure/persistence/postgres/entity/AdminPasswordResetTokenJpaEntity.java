package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity;

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
@Table(name = "\"admin_password_reset_token\"")
public class AdminPasswordResetTokenJpaEntity {
    @Id
    @Column(name = "password_reset_token_id")
    private UUID id;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "token_hash")
    private String tokenHash;

    @Column(name = "used")
    private boolean used;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
