package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyResetCodeRequest(
        @NotBlank
        @NotNull
        @Email
        String email,

        @NotBlank
        @NotNull
        @Size(min = 6, max = 6)
        String code
) {
}
