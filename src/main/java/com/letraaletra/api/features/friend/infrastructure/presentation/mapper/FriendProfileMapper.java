package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendProfileResponse;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.BanInfoResponseMapper;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.InventoryItemResponseMapper;

public class FriendProfileMapper {
    public static FriendProfileResponse toResponse(User user) {
        return new FriendProfileResponse(
                user.getUserId(),
                user.getUsername(),
                !user.isNotInGame(),
                user.getCurrentGameId(),
                user.getStats(),
                BanInfoResponseMapper.toResponse(user.getBanInfo()),
                user.getInventory()
                        .getItems().stream()
                        .filter(InventoryItem::equipped)
                        .map(InventoryItemResponseMapper::toResponse)
                        .toList()
        );
    }
}
