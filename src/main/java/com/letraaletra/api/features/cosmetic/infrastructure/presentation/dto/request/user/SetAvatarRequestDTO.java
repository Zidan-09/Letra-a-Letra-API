package com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record SetAvatarRequestDTO(
        @NotBlank
        String avatar
) {
}
