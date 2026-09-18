package com.letraaletra.api.features.items.domain.catalog;

public record ItemFilter(
        ItemKind kind,
        EquippableCategory category,
        Boolean available
) {
}
