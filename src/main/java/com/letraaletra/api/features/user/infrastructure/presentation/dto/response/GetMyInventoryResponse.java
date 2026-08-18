package com.letraaletra.api.features.user.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;

import java.util.List;

public record GetMyInventoryResponse(
        List<InventoryItemResponse> inventory
) {
}
