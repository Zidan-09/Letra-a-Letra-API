package com.letraaletra.api.presentation.dto.response.user;

public record CreateUserResponseDTO(
        String id,
        String nickname,
        String avatar,
        String email
) {
}
