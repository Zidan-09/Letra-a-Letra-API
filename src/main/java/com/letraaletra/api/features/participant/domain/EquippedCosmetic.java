package com.letraaletra.api.features.participant.domain;

import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
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
            Function<UUID, ItemDefinition> lookup,
            ItemContext context
    ) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .filter(UserItem::isEquipped)
                .filter(item -> lookup.apply(item.getDefinitionId()).isApplicableTo(context))
                .filter(item -> isProfileCategory(lookup.apply(item.getDefinitionId()).getCategory()))
                .map(item -> {
                    ItemDefinition definition = lookup.apply(item.getDefinitionId());
                    return new EquippedCosmetic(
                            definition.getId(),
                            definition.getName(),
                            definition.getCategory(),
                            true,
                            definition.getAssetPath()
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
