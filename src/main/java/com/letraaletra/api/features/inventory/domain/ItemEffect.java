package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InvalidItemException;

public record ItemEffect(
        EffectType type,
        int magnitude,
        int durationMinutes
) {
    public ItemEffect {
        if (type == null) {
            throw new InvalidItemException();
        }

        if (magnitude <= 0) {
            throw new InvalidItemException();
        }

        if (durationMinutes <= 0) {
            throw new InvalidItemException();
        }
    }
}
