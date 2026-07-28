package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticResponse;

public class CosmeticResponseMapper {
    public static CosmeticResponse toDto(Cosmetic cosmetic) {
        return new CosmeticResponse(
            cosmetic.getId().toString(),
            cosmetic.getName(),
            cosmetic.getType(),
            cosmetic.getAssetPath(),
            cosmetic.isAvailable()
        );
    }
}
