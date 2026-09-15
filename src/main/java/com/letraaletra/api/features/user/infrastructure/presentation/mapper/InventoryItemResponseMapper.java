package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;

import java.util.List;
import java.util.UUID;

public class InventoryItemResponseMapper {
    public static InventoryItemResponse toResponse(UserItem item, ItemDefinition definition) {
        return new InventoryItemResponse(
                definition.getId(),
                definition.getName(),
                definition.getKind(),
                definition.getCategory(),
                definition.getApplicability().stream().findFirst().orElse(ItemContext.PROFILE),
                item.getQuantity(),
                item.isEquipped(),
                definition.getAssetPath()
        );
    }

    public static InventoryItemResponse toResponse(com.letraaletra.api.features.user.application.output.EquippedItem equipped) {
        return toResponse(equipped.item(), equipped.definition());
    }

    public static List<InventoryItemResponse> toResponses(List<com.letraaletra.api.features.user.application.output.EquippedItem> equipped) {
        if (equipped == null) {
            return List.of();
        }
        return equipped.stream().map(InventoryItemResponseMapper::toResponse).toList();
    }

    public static List<InventoryItemResponse> toEquippedResponses(List<UserItem> items, java.util.function.Function<UUID, ItemDefinition> lookup) {
        return items.stream()
                .filter(UserItem::isEquipped)
                .map(item -> toResponse(item, lookup.apply(item.getDefinitionId())))
                .toList();
    }

}
