package com.letraaletra.api.features.friend.application.input;

import java.util.UUID;

public record SendFriendRequestInput(
        UUID userId,
        String senderNickname,
        UUID friendId
) {
}
