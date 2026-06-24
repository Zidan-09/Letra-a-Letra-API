package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;

import java.util.UUID;

public class AcceptFriendRequestMapper {
    public static AcceptFriendRequestInput toInput(String userId, String friendId) {
        return new AcceptFriendRequestInput(
                UUID.fromString(userId),
                UUID.fromString(friendId)
        );
    }
}
