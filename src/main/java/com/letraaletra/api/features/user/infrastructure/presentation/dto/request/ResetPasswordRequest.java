package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank
        @NotNull
        @Email
        String email,

        @NotBlank
        @NotNull
        @Size(min = 8, max = 16)
        String newPassword,

        @NotBlank
        @NotNull
        @Size(min = 6, max = 6)
        String code
) {
}
