package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record RevokeUserWalletInput(
        AuthenticatedUser principal,
        UUID userId,
        CoinType type,
        int amount
) {
}
