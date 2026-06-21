package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateCosmeticRequest(
        String id,
        String name,
        MultipartFile asset
) {
}
