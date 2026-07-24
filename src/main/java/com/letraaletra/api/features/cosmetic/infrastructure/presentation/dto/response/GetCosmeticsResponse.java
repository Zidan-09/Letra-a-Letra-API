package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

public record GetCosmeticsResponse(
        PageResponse<CosmeticDTO> cosmetics
) {
}
