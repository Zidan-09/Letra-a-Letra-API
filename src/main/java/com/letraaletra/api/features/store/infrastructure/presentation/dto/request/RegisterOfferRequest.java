package com.letraaletra.api.features.store.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.user.domain.wallet.CoinType;

public record RegisterOfferRequest(
        String title,
        CoinType coinType,
        int price,
        String cosmeticId,
        int rewardSoftCoins,
        int rewardHardGems,
        long expiresIn
) {
}
