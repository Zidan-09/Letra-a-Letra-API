package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.catalog.ItemFilter;
import com.letraaletra.api.features.items.domain.catalog.ItemsPage;
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
