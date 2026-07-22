package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin;

import java.util.UUID;

public record AdminDTO(
        UUID id,
        String username,
        String email
) {
}
