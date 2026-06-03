package com.letraaletra.api.features.user.application.output;

public record FindUserOutput(
        String id,
        String nickname,
        String avatar,
        String email
) {
}
