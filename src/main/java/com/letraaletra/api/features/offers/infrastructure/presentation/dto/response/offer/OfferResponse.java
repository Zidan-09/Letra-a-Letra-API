package com.letraaletra.api.features.offers.infrastructure.presentation.dto.response.offer;

import com.letraaletra.api.features.offers.domain.CoinType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OfferResponse(
        UUID offerId,
        String title,
        CoinType coinType,
        BigDecimal price,
        List<OfferRewardResponse> rewards,
        boolean active,
        boolean hasExpiration,
        LocalDateTime expiresAt
) {
}
