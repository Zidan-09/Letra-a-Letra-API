package com.letraaletra.api.features.friend.application.input;

public record AcceptFriendRequestInput(
        String userId,
        String friendId
) {
}
