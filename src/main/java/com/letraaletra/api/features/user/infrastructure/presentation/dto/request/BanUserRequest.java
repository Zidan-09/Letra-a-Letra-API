package com.letraaletra.api.features.user.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.user.domain.ban.BanType;
import jakarta.validation.constraints.AssertTrue;
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
    private static final int MIN_TEMPORARY_EXPIRES_IN_MINUTES = 1;
    private static final int MAX_TEMPORARY_EXPIRES_IN_MINUTES = 525600;

    @AssertTrue(message = "expiresIn deve estar entre 1 e 525600 minutos para ban TEMPORARY")
    public boolean isExpiresInValid() {
        if (type != BanType.TEMPORARY) {
            return true;
        }

        return expiresIn >= MIN_TEMPORARY_EXPIRES_IN_MINUTES
                && expiresIn <= MAX_TEMPORARY_EXPIRES_IN_MINUTES;
    }
}
