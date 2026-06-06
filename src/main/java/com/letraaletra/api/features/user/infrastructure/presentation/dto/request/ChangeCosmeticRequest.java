package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeCosmeticRequest(
        @NotBlank
        String cosmeticId
) {
}
