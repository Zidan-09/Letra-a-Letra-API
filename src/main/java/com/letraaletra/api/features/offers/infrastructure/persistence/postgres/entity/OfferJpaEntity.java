package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.offers.domain.CoinType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"offer\"")
public class OfferJpaEntity {
    @Id
    @Column(name = "offer_id", nullable = false)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "coin_type")
    private CoinType coinType;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "active")
    private boolean active;

    @Column(name = "repeatable")
    private boolean repeatable;

    @Column(name = "has_expiration")
    private boolean hasExpiration;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
