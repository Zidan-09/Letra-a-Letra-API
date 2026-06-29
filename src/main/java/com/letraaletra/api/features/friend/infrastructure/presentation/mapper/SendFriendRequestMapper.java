package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.SendFriendRequestResponse;
import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public class SendFriendRequestMapper {
    public static SendFriendRequestInput toInput(User user, String friendId) {
        return new SendFriendRequestInput(
                user.getId(),
                user.getNickname(),
                UUID.fromString(friendId)
        );
    }

    public static SendFriendRequestResponse toResponse(SendFriendRequestOutput output) {
        return new SendFriendRequestResponse(
                output.friend()
        );
    }
}
