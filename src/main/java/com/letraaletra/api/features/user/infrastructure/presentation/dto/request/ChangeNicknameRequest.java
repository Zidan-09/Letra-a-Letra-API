package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeNicknameRequest(
        @NotBlank
        @Size(min = 5, max = 10)
        String nickname
) {
}
