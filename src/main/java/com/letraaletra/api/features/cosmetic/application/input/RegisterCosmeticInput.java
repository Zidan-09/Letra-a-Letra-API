package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record RegisterCosmeticInput(
        UUID auth,
        String name,
        CosmeticTypes type,
        MultipartFile asset
) {
}
