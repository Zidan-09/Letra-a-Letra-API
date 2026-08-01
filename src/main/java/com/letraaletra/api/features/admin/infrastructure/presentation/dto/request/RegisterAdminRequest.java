package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterAdminRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email
) {
}
