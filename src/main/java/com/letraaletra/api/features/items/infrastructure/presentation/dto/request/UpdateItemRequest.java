package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

public record UpdateItemRequest(
        String name,
        Boolean available,
        boolean isNewAsset
) {
}
