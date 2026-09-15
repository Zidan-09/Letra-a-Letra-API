package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemEffect;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.Set;

public record CreateItemDefinitionInput(
        AuthenticatedUser principal,
        String name,
        ItemKind kind,
        ItemCategory category,
        Set<ItemContext> applicability,
        boolean stackable,
        Integer maxStack,
        boolean consumable,
        ItemEffect effect,
        ItemAssetUpload asset
) {
}
