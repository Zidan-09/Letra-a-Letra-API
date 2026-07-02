package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;

public class CosmeticMapper {
    public static CosmeticDTO toDto(Cosmetic cosmetic) {
        return new CosmeticDTO(
            cosmetic.getId().toString(),
            cosmetic.getName(),
            cosmetic.getType(),
            cosmetic.getAssetPath(),
            cosmetic.isAvailable()
        );
    }
}
