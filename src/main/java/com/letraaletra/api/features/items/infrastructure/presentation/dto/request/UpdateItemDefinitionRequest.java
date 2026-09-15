package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

public record UpdateItemDefinitionRequest(
        String name,
        Boolean available,
        boolean isNewAsset
) {
}
