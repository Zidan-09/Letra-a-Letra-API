package com.letraaletra.api.features.user.domain.wallet;

import com.letraaletra.api.features.offers.domain.CoinType;

public record Balance(
        long coins,
        long gems
) {
    public long getAmountFor(CoinType coinType) {
        return switch (coinType) {
            case SOFT -> coins;
            case HARD -> gems;
            case REAL -> throw new IllegalStateException("Wallet does not track REAL currency balance.");
        };
    }
}
