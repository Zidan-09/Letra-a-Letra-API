package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;

import java.util.List;

public record GetCosmeticsResponse(
        List<Cosmetic> cosmetics,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
