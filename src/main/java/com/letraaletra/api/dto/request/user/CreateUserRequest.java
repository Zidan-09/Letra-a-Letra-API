package com.letraaletra.api.dto.request.user;

public record CreateUserRequest(
        String nickname,
        String email,
        String password
) {
}
