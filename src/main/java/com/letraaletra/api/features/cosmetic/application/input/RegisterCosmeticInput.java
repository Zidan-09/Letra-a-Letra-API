package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.springframework.web.multipart.MultipartFile;

public record RegisterCosmeticInput(
    String id,
    String name,
    CosmeticTypes type,
    MultipartFile asset
) {
}
