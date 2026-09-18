package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.item.ItemKind;

import java.util.UUID;

public record GetUserItemsInput(
        UUID userId,
        ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        Boolean equipped
) {
}
