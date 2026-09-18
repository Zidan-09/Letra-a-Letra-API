package com.letraaletra.api.features.items.domain;

public record ItemFilter(
        ItemKind kind,
        EquippableCategory category,
        Boolean available
) {
}
