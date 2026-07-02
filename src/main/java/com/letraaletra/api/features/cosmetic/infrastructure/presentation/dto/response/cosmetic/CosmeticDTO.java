package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

public record CosmeticDTO(
        String id,
        String name,
        CosmeticTypes type,
        String assetPath,
        boolean available
) {
}
