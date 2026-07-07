package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

public record AuthAdminRequest(
        String email,
        String password
) {
}
