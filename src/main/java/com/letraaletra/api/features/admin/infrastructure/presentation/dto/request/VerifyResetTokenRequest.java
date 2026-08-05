package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyResetTokenRequest(
        @NotBlank
        @NotNull
        @Email
        String email,

        @NotNull
        @NotBlank
        String token
) {
}
