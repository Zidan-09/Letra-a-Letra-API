package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;

public class RejectFriendRequestMapper {
    public static RejectFriendRequestInput toInput(String userId, String friendId) {
        return new RejectFriendRequestInput(
                userId,
                friendId
        );
    }
}
