package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "\"user_active_effect\"")
public class UserActiveEffectJpaEntity {
    @Id
    @Column(name = "effect_id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "effect_type", nullable = false)
    private String effectType;

    @Column(name = "bonus_percentage")
    private Integer bonusPercentage;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "remaining_uses")
    private Integer remainingUses;
}
