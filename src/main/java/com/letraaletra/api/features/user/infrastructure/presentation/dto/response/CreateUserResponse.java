package com.letraaletra.api.features.user.infrastructure.presentation.dto.response;

public record CreateUserResponse(
        String id,
        String avatar,
        String email
) {
}
