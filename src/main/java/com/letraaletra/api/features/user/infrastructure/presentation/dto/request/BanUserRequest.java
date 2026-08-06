package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.user.domain.BanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BanUserRequest(
        @NotNull
        BanType type,

        int expiresIn,

        @NotNull
        @NotBlank
        @Size(max = 500)
        String reason
) {
}
