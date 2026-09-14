package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendProfileResponse;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.BanInfoResponseMapper;

import java.util.List;

public class FriendProfileMapper {
    public static FriendProfileResponse toResponse(User user, List<InventoryItemResponse> equipped) {
        return new FriendProfileResponse(
                user.getUserId(),
                user.getUsername(),
                !user.isNotInGame(),
                user.getCurrentGameId(),
                user.getStats(),
                BanInfoResponseMapper.toResponse(user.getBanInfo()),
                equipped == null ? List.of() : List.copyOf(equipped)
        );
    }
}
