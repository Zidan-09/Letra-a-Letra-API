package com.letraaletra.api.features.offers.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.CoinType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record RegisterOfferRequest(
        @NotBlank
        String title,

        @NotNull
        CoinType coinType,

        @Positive
        BigDecimal price,

        @NotEmpty
        @Valid
        List<RegisterOfferRewardRequest> rewards,

        @NotNull
        boolean hasExpiration,

        @PositiveOrZero
        long expiresIn
) {
}
