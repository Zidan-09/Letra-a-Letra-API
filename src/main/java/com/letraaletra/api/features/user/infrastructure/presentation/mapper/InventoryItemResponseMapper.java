package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.domain.ConsumableItem;
import com.letraaletra.api.features.items.domain.EquippableItem;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;

import java.util.List;
import java.util.UUID;

public class InventoryItemResponseMapper {
    public static InventoryItemResponse toResponse(UserItem owned, Item item) {
        return new InventoryItemResponse(
                item.getId(),
                item.getName(),
                item instanceof ConsumableItem ? ItemKind.CONSUMABLE : ItemKind.EQUIPPABLE,
                item instanceof EquippableItem equippable
                        ? equippable.getCategory()
                        : ((ConsumableItem) item).getCategory(),
                item instanceof EquippableItem equipped
                        ? equipped.getContext()
                        : ((ConsumableItem) item).getContext(),
                owned.getQuantity(),
                owned.isEquipped(),
                item instanceof EquippableItem withAsset ? withAsset.getAssetPath() : null
        );
    }

    public static InventoryItemResponse toResponse(com.letraaletra.api.features.user.application.output.EquippedItem equipped) {
        return toResponse(equipped.owned(), equipped.item());
    }

    public static List<InventoryItemResponse> toResponses(List<com.letraaletra.api.features.user.application.output.EquippedItem> equipped) {
        if (equipped == null) {
            return List.of();
        }
        return equipped.stream().map(InventoryItemResponseMapper::toResponse).toList();
    }

    public static List<InventoryItemResponse> toEquippedResponses(List<UserItem> items, java.util.function.Function<UUID, Item> lookup) {
        return items.stream()
                .filter(UserItem::isEquipped)
                .map(owned -> toResponse(owned, lookup.apply(owned.getItemId())))
                .toList();
    }

}
