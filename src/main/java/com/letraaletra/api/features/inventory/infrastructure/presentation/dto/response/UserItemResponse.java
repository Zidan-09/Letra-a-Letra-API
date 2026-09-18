package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.ItemKind;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserItemResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        ItemCategory category,
        EquippableContext context,
        int quantity,
        boolean equipped,
        LocalDateTime acquiredAt,
        LocalDateTime expiresAt,
        String assetPath
) {
}
