package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"user_wallet\"")
public class UserWalletJpaEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "soft_coins", nullable = false)
    private int softCoins;

    @Column(name = "hard_gems", nullable = false)
    private int hard_gems;
}
