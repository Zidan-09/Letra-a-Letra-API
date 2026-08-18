package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.offer.OfferRewardResponse;
import com.letraaletra.api.shared.infrastructure.presentation.mapper.RewardResponseMapper;

public class OfferRewardResponseMapper {
    public static OfferRewardResponse toResponse(OfferReward offerReward) {
        return new OfferRewardResponse(
                offerReward.offerRewardId(),
                RewardResponseMapper.toResponse(offerReward.reward())
        );
    }
}
