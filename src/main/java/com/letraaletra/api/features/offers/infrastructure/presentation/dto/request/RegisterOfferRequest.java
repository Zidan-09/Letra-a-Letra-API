package com.letraaletra.api.features.offers.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.CoinType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RegisterOfferRequest(
        @NotBlank
        String title,

        @NotBlank
        CoinType coinType,

        @NotNull
        int price,

        @Valid
        List<RegisterOfferRewardRequest> rewards,

        @NotNull
        long expiresIn
) {
}
