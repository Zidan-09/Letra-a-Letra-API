package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetAdminPasswordRequest(
        @NotBlank
        @NotNull
        @Size(min = 8, max = 16)
        String newPassword,

        @NotBlank
        @NotNull
        String token
) {
}
