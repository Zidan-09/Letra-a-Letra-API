package com.letraaletra.api.features.user.domain.wallet;

public record WalletLog(
        String logId,
        CoinType coinType,
        int amount,
        int balanceAfter,
        TransactionType reason
) {
}
