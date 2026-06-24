package com.letraaletra.api.features.friend.application.input;

import java.util.UUID;

public record RemoveFriendInput(
        UUID userId,
        UUID friendId
) {
}
