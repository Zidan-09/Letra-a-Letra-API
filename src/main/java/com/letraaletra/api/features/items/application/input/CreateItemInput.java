package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record CreateItemInput(
        AuthenticatedUser principal,
        String name,
        ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        ItemEffect effect,
        ItemAssetUpload asset
) {
}
