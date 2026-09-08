package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;

import java.util.UUID;

public class RemoveFriendMapper {
    public static RemoveFriendInput toInput(UUID userId, UUID friendId) {
        return new RemoveFriendInput(
                userId,
                friendId
        );
    }
}
