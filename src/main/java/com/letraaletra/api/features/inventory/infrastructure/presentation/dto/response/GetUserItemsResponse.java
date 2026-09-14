package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response;

import java.util.List;

public record GetUserItemsResponse(
        List<UserItemResponse> items
) {
}
