package com.letraaletra.api.features.participant.domain;

import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.inventory.domain.UserItem;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record EquippedCosmetic(
        UUID itemId,
        String name,
        EquippableCategory category,
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

    private static boolean isProfileCategory(EquippableCategory category) {
        return category == EquippableCategory.AVATAR
                || category == EquippableCategory.BANNER
                || category == EquippableCategory.FRAME
                || category == EquippableCategory.EMOTE;
    }
}
