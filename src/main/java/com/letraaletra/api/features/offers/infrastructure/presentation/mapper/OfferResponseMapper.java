package com.letraaletra.api.features.offers.infrastructure.presentation.mapper;

import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.offer.OfferResponse;

public class OfferResponseMapper {
    public static OfferResponse toResponse(Offer offer) {
        return new OfferResponse(
                offer.getOfferId(),
                offer.getTitle(),
                offer.getCoinType(),
                offer.getPrice(),
                offer.getRewards().stream()
                        .map(OfferRewardResponseMapper::toResponse)
                        .toList(),
                offer.isActive(),
                offer.isRepeatable(),
                offer.isHasExpiration(),
                offer.getExpiresAt()
        );
    }
}
