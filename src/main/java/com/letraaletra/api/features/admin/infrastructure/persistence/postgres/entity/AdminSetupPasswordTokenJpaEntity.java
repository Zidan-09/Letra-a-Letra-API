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
@Table(name = "\"admin_setup_password_token\"")
public class AdminSetupPasswordTokenJpaEntity {
    @Id
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;
}
