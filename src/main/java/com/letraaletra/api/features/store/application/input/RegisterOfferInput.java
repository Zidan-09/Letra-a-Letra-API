package com.letraaletra.api.features.store.application.input;

import com.letraaletra.api.features.user.domain.wallet.CoinType;

public record RegisterOfferInput(
        String title,
        CoinType coinType,
        int price,
        String cosmeticId,
        int rewardSoftCoins,
        int rewardHardGems
) {
}
