package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateCosmeticInput(
        UUID auth,
        UUID id,
        String name,
        CosmeticTypes type,
        MultipartFile asset,
        boolean isNewAsset
) {
}
