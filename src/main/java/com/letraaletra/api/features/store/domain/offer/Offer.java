package com.letraaletra.api.features.store.domain.offer;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.user.domain.wallet.CoinType;

import java.time.LocalDateTime;

public record Offer(
        String offer_id,
        String title,
        CoinType coinType,
        int price,
        Cosmetic cosmetic,
        int rewardSoftCoins,
        int rewardHardGems,
        boolean active,
        LocalDateTime expiresAt
) {
}
