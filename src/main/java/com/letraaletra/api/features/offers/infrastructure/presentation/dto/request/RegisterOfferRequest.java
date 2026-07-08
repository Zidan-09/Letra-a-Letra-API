package com.letraaletra.api.features.offers.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.CoinType;

import java.util.List;

public record RegisterOfferRequest(
        String title,
        CoinType coinType,
        int price,
        List<RegisterOfferRewardRequest> rewards,
        long expiresIn
) {
}
