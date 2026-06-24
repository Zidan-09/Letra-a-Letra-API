package com.letraaletra.api.features.friend.application.input;

import java.util.UUID;

public record RejectFriendRequestInput(
        UUID userId,
        UUID friendId
) {
}
