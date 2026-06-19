package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.SendFriendRequestResponse;

public class SendFriendRequestMapper {
    public static SendFriendRequestInput toInput(String userId, String friendId) {
        return new SendFriendRequestInput(
                userId,
                friendId
        );
    }

    public static SendFriendRequestResponse toResponse(SendFriendRequestOutput output) {
        return new SendFriendRequestResponse(
                output.friend()
        );
    }
}
