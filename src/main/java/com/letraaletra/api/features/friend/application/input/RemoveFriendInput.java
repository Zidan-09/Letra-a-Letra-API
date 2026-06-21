package com.letraaletra.api.features.friend.application.input;

public record RemoveFriendInput(
        String userId,
        String friendId
) {
}
