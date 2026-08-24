package com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.offer;

import com.letraaletra.api.features.reward.infrastructure.presentation.dto.response.RewardResponse;

import java.util.UUID;

public record OfferRewardResponse(
        UUID offerRewardId,
        RewardResponse reward
) {
}
