package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 16)
        String newPassword,

        @NotBlank
        @Size(min = 6, max = 6)
        String code
) {
}
