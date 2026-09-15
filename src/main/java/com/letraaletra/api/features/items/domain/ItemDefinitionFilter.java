package com.letraaletra.api.features.items.domain;

public record ItemDefinitionFilter(
        ItemKind kind,
        ItemCategory category,
        Boolean available
) {
}
