package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateCosmeticInput(
        User user,
        UUID id,
        String name,
        CosmeticTypes type,
        MultipartFile asset,
        boolean isNewAsset
) {
}
