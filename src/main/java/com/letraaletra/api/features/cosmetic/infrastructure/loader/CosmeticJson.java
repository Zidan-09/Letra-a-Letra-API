package com.letraaletra.api.features.cosmetic.infrastructure.loader;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

public record CosmeticJson(
        String id,
        String name,
        CosmeticTypes type
) {
}
