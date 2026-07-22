package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String nickname,
        String email
) {
}
