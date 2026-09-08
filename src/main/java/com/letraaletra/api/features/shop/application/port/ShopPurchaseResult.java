package com.letraaletra.api.features.shop.application.port;

import java.util.List;
import java.util.UUID;

public record ShopPurchaseResult(
        List<UUID> transactionIds,
        long newSoftCoins,
        long newHardGems
) {
}
