package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;

public class RemoveFriendMapper {
    public static RemoveFriendInput toInput(String userId, String friendId) {
        return new RemoveFriendInput(
                userId,
                friendId
        );
    }
}
