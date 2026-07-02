package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;

import java.util.List;

public record GetCosmeticsResponse(
        List<CosmeticDTO> cosmetics,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
