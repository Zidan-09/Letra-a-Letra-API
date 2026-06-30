package com.letraaletra.api.features.cosmetic.application.input;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateCosmeticInput(
        UUID id,
        String name,
        MultipartFile asset
) {
}
