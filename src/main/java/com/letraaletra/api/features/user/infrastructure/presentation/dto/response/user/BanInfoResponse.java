package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import com.letraaletra.api.features.user.domain.BanType;

import java.time.LocalDateTime;

public record BanInfoResponse(
        boolean banned,
        BanType type,
        String reason,
        LocalDateTime expiresAt
) {
}
