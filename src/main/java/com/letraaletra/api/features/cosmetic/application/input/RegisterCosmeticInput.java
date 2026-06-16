package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

public record RegisterCosmeticInput(
    String id,
    String name,
    CosmeticTypes type
) {
}
