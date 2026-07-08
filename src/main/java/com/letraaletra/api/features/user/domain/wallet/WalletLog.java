package com.letraaletra.api.features.user.domain.wallet;

import com.letraaletra.api.features.offers.domain.CoinType;

public record WalletLog(
        String logId,
        CoinType coinType,
        int amount,
        int balanceAfter,
        TransactionType reason
) {
}
