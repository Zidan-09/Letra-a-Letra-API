package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.CoinType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RevokeUserWalletRequest(
        @NotNull
        CoinType type,

        @NotNull
        @Positive
        int amount
) {
}
