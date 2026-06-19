package com.letraaletra.api.features.friend.application.input;

public record SendFriendRequestInput(
        String userId,
        String friendId
) {
}
