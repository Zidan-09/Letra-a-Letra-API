package com.letraaletra.api.features.offers.application.input;

import com.letraaletra.api.features.offers.domain.CoinType;

import java.util.List;
import java.util.UUID;

public record RegisterOfferInput(
        UUID auth,
        String title,
        CoinType coinType,
        int price,
        List<RegisterOfferRewardInput> rewards,
        long expiresIn
) {
}
