package com.letraaletra.api.features.items.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemKind;

import java.util.Set;
import java.util.UUID;

public record ItemDefinitionResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        ItemCategory category,
        Set<ItemContext> contexts,
        boolean stackable,
        Integer maxStack,
        boolean consumable,
        ItemEffectResponse effect,
        String assetPath,
        int version,
        boolean available
) {
    public record ItemEffectResponse(
            EffectType type,
            int magnitude,
            int durationMinutes
    ) {
    }
}
