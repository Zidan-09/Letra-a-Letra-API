package com.letraaletra.api.features.offers.application.input;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.math.BigDecimal;
import java.util.List;

public record RegisterOfferInput(
        AuthenticatedUser principal,
        String title,
        CoinType coinType,
        BigDecimal price,
        List<RegisterOfferRewardInput> rewards,
        boolean repeatable,
        boolean hasExpiration,
        long expiresIn
) {
}
