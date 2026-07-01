package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record RegisterCosmeticRequest(
        @NotBlank
        String name,

        @NotNull
        CosmeticTypes cosmeticType,

        @NotNull
        MultipartFile asset
) {
}
