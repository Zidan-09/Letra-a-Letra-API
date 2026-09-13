package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.CancelFriendRequestInput;

import java.util.UUID;

public class CancelFriendRequestMapper {
    public static CancelFriendRequestInput toInput(UUID userId, UUID friendId) {
        return new CancelFriendRequestInput(
                userId,
                friendId
        );
    }
}
