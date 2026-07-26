package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

import java.util.UUID;

public record InventoryItemResponse(
        UUID cosmeticId,
        String name,
        CosmeticTypes type,
        boolean equipped
) {
}
