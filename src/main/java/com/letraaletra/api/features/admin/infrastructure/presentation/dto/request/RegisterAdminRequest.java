package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

public record RegisterAdminRequest(
        String name,
        String email,
        String password
) {
}
