package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserResponse;

import java.util.List;

public class UserResponseMapper {
    public static UserResponse toResponse(User user, List<InventoryItemResponse> equipped) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                BanInfoResponseMapper.toResponse(user.getBanInfo()),
                user.getStats(),
                equipped == null ? List.of() : List.copyOf(equipped),
                WalletResponseMapper.toResponse(user.getWallet())
        );
    }
}
