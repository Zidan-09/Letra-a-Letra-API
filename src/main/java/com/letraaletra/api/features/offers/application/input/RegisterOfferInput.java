package com.letraaletra.api.features.offers.application.input;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.List;

public record RegisterOfferInput(
        AuthenticatedUser principal,
        String title,
        CoinType coinType,
        int price,
        List<RegisterOfferRewardInput> rewards,
        long expiresIn
) {
}
