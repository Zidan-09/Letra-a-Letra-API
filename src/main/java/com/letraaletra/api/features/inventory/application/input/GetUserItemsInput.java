package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.items.domain.EquippableCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.ItemKind;

import java.util.UUID;

public record GetUserItemsInput(
        UUID userId,
        ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        Boolean equipped
) {
}
