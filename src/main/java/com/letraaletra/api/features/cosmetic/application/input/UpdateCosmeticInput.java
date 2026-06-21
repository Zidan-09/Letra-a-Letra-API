package com.letraaletra.api.features.cosmetic.application.input;

import org.springframework.web.multipart.MultipartFile;

public record UpdateCosmeticInput(
        String id,
        String name,
        MultipartFile asset
) {
}
