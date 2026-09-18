package com.letraaletra.api.features.user.domain.effect;

import java.time.Instant;
import java.util.Objects;

public interface TimeBasedEffect extends UserEffect {
    Instant getExpiresAt();

    default boolean isValid(Instant now) {
        Objects.requireNonNull(now);
        return getExpiresAt().isAfter(now);
    }

    default boolean isExpired(Instant now) {
        return !isValid(now);
    }
}
