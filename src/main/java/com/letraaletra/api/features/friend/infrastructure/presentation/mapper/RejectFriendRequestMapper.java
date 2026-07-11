package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;

import java.util.UUID;

public class RejectFriendRequestMapper {
    public static RejectFriendRequestInput toInput(UUID userId, String friendId) {
        return new RejectFriendRequestInput(
                userId,
                UUID.fromString(friendId)
        );
    }
}
