package com.letraaletra.api.features.friend.application.input;

public record RejectFriendRequestInput(
        String userId,
        String friendId
) {
}
