package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;

import java.util.UUID;

public class RejectFriendRequestMapper {
    public static RejectFriendRequestInput toInput(String userId, String friendId) {
        return new RejectFriendRequestInput(
                UUID.fromString(userId),
                UUID.fromString(friendId)
        );
    }
}
