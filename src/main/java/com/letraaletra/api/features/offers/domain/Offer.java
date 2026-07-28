package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.features.offers.domain.exception.InvalidOfferExpirationException;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Offer {
    private final UUID offerId;
    private final String title;
    private final CoinType coinType;
    private final BigDecimal price;
    private final List<OfferReward> rewards;
    private final boolean repeatable;
    private boolean active;
    private final boolean hasExpiration;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    public Offer(
            UUID offerId,
            String title,
            CoinType coinType,
            BigDecimal price,
            List<OfferReward> rewards,
            boolean active,
            boolean repeatable,
            boolean hasExpiration,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {
        this.offerId = offerId;
        this.title = title;
        this.coinType = coinType;
        this.price = price;
        this.rewards = rewards;
        this.active = active;
        this.repeatable = repeatable;
        this.hasExpiration = hasExpiration;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static Offer create(
            String title,
            CoinType coinType,
            BigDecimal price,
            List<OfferReward> rewards,
            boolean repeatable,
            boolean hasExpiration,
            long expiresIn
    ) {
        if (hasExpiration && expiresIn <= 0) {
            throw new InvalidOfferExpirationException();
        }

        return new Offer(
                UUID.randomUUID(),
                title,
                coinType,
                price,
                rewards,
                false,
                repeatable,
                hasExpiration,
                hasExpiration ?
                        LocalDateTime.now().plusMinutes(expiresIn) :
                        null,
                LocalDateTime.now()
        );
    }

    public static Offer restore(
            UUID id,
            String title,
            CoinType coinType,
            BigDecimal price,
            List<OfferReward> rewards,
            boolean active,
            boolean repeatable,
            boolean hasExpiration,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {
        return new Offer(
                id,
                title,
                coinType,
                price,
                rewards,
                active,
                repeatable,
                hasExpiration,
                expiresAt,
                createdAt
        );
    }

    public UUID getOfferId() {
        return offerId;
    }

    public String getTitle() {
        return title;
    }

    public CoinType getCoinType() {
        return coinType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public List<OfferReward> getRewards() {
        return List.copyOf(rewards);
    }

    public boolean isHasExpiration() {
        return hasExpiration;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void disable() {
        if (!active) {
            throw new InvalidOfferStatusException();
        }

        active = false;
    }

    public void enable() {
        if (
                active ||
                hasExpiration && LocalDateTime.now().isAfter(expiresAt)
        ) {
            throw new InvalidOfferStatusException();
        }

        active = true;
    }
}
