package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserProfileResponse;

public class UserProfileResponseMapper {
    public static UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getStats(),
                user.getInventory()
                        .getItems().stream()
                        .filter(InventoryItem::equipped)
                        .map(InventoryItemResponseMapper::toResponse)
                        .toList(),
                WalletResponseMapper.toResponse(user.getWallet())
        );
    }
}
