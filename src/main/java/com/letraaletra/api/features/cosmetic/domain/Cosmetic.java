package com.letraaletra.api.features.cosmetic.domain;

public record Cosmetic(
        String id,
        String name,
        CosmeticTypes type,
        String assetPath,
        int version
) {
}
