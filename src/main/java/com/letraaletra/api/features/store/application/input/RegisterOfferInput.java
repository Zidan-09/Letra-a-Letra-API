package com.letraaletra.api.features.store.application.input;

import com.letraaletra.api.features.user.domain.wallet.CoinType;

import java.util.UUID;

public record RegisterOfferInput(
        UUID auth,
        String title,
        CoinType coinType,
        int price,
        UUID cosmeticId,
        int rewardSoftCoins,
        int rewardHardGems,
        long expiresIn
) {
}
