package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.output.UserItemDetails;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.UserItemResponse;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.item.ItemKind;

public class UserItemResponseMapper {
    public static UserItemResponse toResponse(UserItemDetails details) {
        Item item = details.item();

        return new UserItemResponse(
                item.getId(),
                item.getName(),
                item instanceof ConsumableItem ? ItemKind.CONSUMABLE : ItemKind.EQUIPPABLE,
                item instanceof EquippableItem equippable
                        ? equippable.getCategory()
                        : null,
                item instanceof EquippableItem equipped
                        ? equipped.getContext()
                        : null,
                details.owned().getQuantity(),
                details.owned().isEquipped(),
                details.owned().getAcquiredAt(),
                details.owned().getExpiresAt(),
                item instanceof EquippableItem withAsset ? withAsset.getAssetPath() : null
        );
    }
}
