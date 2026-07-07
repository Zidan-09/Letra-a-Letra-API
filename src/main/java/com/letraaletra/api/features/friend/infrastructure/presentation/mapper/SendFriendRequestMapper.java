package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.SendFriendRequestResponse;

import java.util.UUID;

public class SendFriendRequestMapper {
    public static SendFriendRequestInput toInput(UUID auth, String friendId) {
        return new SendFriendRequestInput(
                auth,
                UUID.fromString(friendId)
        );
    }

    public static SendFriendRequestResponse toResponse(SendFriendRequestOutput output) {
        return new SendFriendRequestResponse(
                output.friend()
        );
    }
}
