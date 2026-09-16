package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.output.UserItemDetails;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.UserItemResponse;

public class UserItemResponseMapper {
    public static UserItemResponse toResponse(UserItemDetails details) {
        return new UserItemResponse(
                details.definition().getId(),
                details.definition().getName(),
                details.definition().getKind(),
                details.definition().getCategory(),
                details.definition().getContext(),
                details.item().getQuantity(),
                details.item().isEquipped(),
                details.item().getAcquiredAt(),
                details.item().getExpiresAt(),
                details.definition().getAssetPath()
        );
    }
}
