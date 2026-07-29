package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin;

import java.util.UUID;

public record AdminResponse(
        UUID id,
        String username,
        String email
) {
}
