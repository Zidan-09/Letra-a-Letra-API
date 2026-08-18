package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

public record RegisterCosmeticInput(
        AuthenticatedUser principal,
        String name,
        CosmeticTypes type,
        MultipartFile asset
) {
}
