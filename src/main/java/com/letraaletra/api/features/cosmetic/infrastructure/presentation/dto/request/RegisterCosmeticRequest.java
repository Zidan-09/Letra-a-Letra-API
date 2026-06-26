package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.springframework.web.multipart.MultipartFile;

public record RegisterCosmeticRequest(
        String id,
        String name,
        CosmeticTypes cosmeticType,
        MultipartFile asset
) {
}
