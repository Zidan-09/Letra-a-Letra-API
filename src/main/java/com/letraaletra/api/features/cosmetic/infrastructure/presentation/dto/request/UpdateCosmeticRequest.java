package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.springframework.web.multipart.MultipartFile;

public record UpdateCosmeticRequest(
        String name,
        CosmeticTypes type,
        MultipartFile asset
) {
}
