package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeNicknameRequest(
        @NotBlank
        @NotNull
        @Size(min = 3, max = 16)
        String nickname
) {
}
