package com.letraaletra.api.features.user.domain.ban;

import com.letraaletra.api.features.user.domain.BanType;

import java.time.LocalDateTime;

public record BanInfo(
        BanType type,
        String reason,
        LocalDateTime expiresAt
) {
    public static BanInfo create() {
        return new BanInfo(
                null,
                null,
                null
        );
    }

    public static BanInfo restore(
            BanType type,
            String reason,
            LocalDateTime expiresAt
    ) {
        return new BanInfo(
                type,
                reason,
                expiresAt
        );
    }

    public static BanInfo ban(LocalDateTime expiresAt, String reason) {
        return new BanInfo(
                expiresAt != null ? BanType.TEMPORARY : BanType.PERMANENT,
                reason,
                expiresAt
        );
    }
}
