package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;

public record UpdateCosmeticResponse(
        Cosmetic cosmetic
) {
}
