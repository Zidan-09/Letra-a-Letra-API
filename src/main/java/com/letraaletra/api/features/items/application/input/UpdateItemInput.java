package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record UpdateItemInput(
        AuthenticatedUser principal,
        UUID itemId,
        String name,
        Boolean available,
        EquippableCategory category,
        EquippableContext context,
        ItemEffect effect,
        ItemAssetUpload asset,
        boolean isNewAsset
) {
}
