package com.letraaletra.api.features.user.application.output;

public record CreateUserOutput(
        String id,
        String nickname,
        String avatar,
        String email
) {
}
