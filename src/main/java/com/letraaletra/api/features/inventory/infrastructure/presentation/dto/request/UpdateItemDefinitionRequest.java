package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request;

public record UpdateItemDefinitionRequest(
        String name,
        String assetPath,
        Boolean available
) {
}
