package com.letraaletra.api.features.store.domain;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.store.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.user.domain.wallet.CoinType;

import java.time.LocalDateTime;

public class StoreOffer {
    private final String offerId;
    private final String title;
    private final CoinType coinType;
    private final int price;
    private final Cosmetic cosmetic;
    private final int rewardSoftCoins;
    private final int rewardHardGems;
    private boolean active;
    private final LocalDateTime expiresAt;

    public StoreOffer(
            String offerId,
            String title,
            CoinType coinType,
            int price,
            Cosmetic cosmetic,
            int rewardSoftCoins,
            int rewardHardGems,
            boolean active,
            LocalDateTime expiresAt
    ) {
        this.offerId = offerId;
        this.title = title;
        this.coinType = coinType;
        this.price = price;
        this.cosmetic = cosmetic;
        this.rewardSoftCoins = rewardSoftCoins;
        this.rewardHardGems = rewardHardGems;
        this.active = active;
        this.expiresAt = expiresAt;
    }

    public String getOfferId() {
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

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    public int getRewardSoftCoins() {
        return rewardSoftCoins;
    }

    public int getRewardHardGems() {
        return rewardHardGems;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            throw new InvalidOfferStatusException();
        }

        this.active = active;
    }
}
