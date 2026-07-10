package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthAdminRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 32)
        String password
) {
}
