package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemEffect;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record CreateItemDefinitionInput(
        AuthenticatedUser principal,
        String name,
        ItemKind kind,
        ItemCategory category,
        ItemContext context,
        boolean consumable,
        ItemEffect effect,
        ItemAssetUpload asset
) {
}
