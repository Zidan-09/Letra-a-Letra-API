package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshSessionRequest(
        @NotBlank
        String refreshToken
) {
}
