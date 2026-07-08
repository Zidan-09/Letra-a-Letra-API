package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Offer {
    private final UUID offerId;
    private final String title;
    private final CoinType coinType;
    private final int price;
    private final List<OfferReward> rewards;
    private boolean active;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    public Offer(
            UUID offerId,
            String title,
            CoinType coinType,
            int price,
            List<OfferReward> rewards,
            boolean active,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {
        this.offerId = offerId;
        this.title = title;
        this.coinType = coinType;
        this.price = price;
        this.rewards = rewards;
        this.active = active;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static Offer create(
            String title,
            CoinType coinType,
            int price,
            List<OfferReward> rewards,
            boolean active,
            LocalDateTime expiresAt
    ) {
        return new Offer(
                UUID.randomUUID(),
                title,
                coinType,
                price,
                rewards,
                active,
                expiresAt,
                LocalDateTime.now()
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

    public int getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public List<OfferReward> getRewards() {
        return rewards;
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
        if (active) {
            throw new InvalidOfferStatusException();
        }

        active = true;
    }
}
