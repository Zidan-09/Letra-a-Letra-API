package com.letraaletra.api.features.items.domain;

public record ItemFilter(
        ItemKind kind,
        ItemCategory category,
        Boolean available
) {
}
