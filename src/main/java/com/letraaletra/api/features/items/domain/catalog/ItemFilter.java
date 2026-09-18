package com.letraaletra.api.features.items.domain.catalog;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.item.ItemKind;

public record ItemFilter(
        ItemKind kind,
        EquippableCategory category,
        Boolean available
) {
}
