package com.letraaletra.api.features.user.domain.wallet;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.transaction.domain.OperationType;

public record WalletMovement(
        CoinType coinType,
        int amount,
        OperationType operation
) {}
