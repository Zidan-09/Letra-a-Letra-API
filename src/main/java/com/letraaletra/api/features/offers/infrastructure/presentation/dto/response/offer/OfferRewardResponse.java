package com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.offer;

import com.letraaletra.api.shared.infrastructure.presentation.dto.response.reward.RewardResponse;

import java.util.UUID;

public record OfferRewardResponse(
        UUID offerRewardId,
        RewardResponse reward
) {
}
