package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

public record RegisterCosmeticRequest(
        String id,
        String name,
        CosmeticTypes cosmeticType
) {
}
