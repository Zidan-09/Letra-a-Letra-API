package com.letraaletra.api.features.participant.domain;

import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.features.inventory.domain.UserItem;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record EquippedCosmetic(
        UUID itemId,
        String name,
        ItemCategory category,
        boolean equipped,
        String assetPath
) {
    public static List<EquippedCosmetic> fromProfileItems(
            List<UserItem> items,
            Function<UUID, Item> lookup,
            EquippableContext context
    ) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .filter(UserItem::isEquipped)
                .filter(item -> lookup.apply(item.getItemId()) instanceof EquippableItem equippable
                        && equippable.getContext() == context
                        && isProfileCategory(equippable.getCategory()))
                .map(item -> {
                    EquippableItem equippable = (EquippableItem) lookup.apply(item.getItemId());
                    return new EquippedCosmetic(
                            equippable.getId(),
                            equippable.getName(),
                            equippable.getCategory(),
                            true,
                            equippable.getAssetPath()
                    );
                })
                .toList();
    }

    private static boolean isProfileCategory(ItemCategory category) {
        return category == ItemCategory.AVATAR
                || category == ItemCategory.BANNER
                || category == ItemCategory.FRAME
                || category == ItemCategory.EMOTE;
    }
}
