package com.letraaletra.api.dto.request.user;

public record CreateUserRequestDTO(
        String nickname,
        String avatar,
        String email,
        String password
) {
}
