package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyResetTokenRequest(
        @NotNull
        @NotBlank
        String token
) {
}
