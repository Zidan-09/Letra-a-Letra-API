package com.letraaletra.api.features.user.application.input;

public record SetAvatarInput(
        String userId,
        String avatar
) {
}
