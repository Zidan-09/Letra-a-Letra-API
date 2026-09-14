package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemKind;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserItemResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        ItemCategory category,
        Set<ItemContext> contexts,
        int quantity,
        boolean equipped,
        LocalDateTime acquiredAt,
        LocalDateTime expiresAt,
        String assetPath
) {
}
