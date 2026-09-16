package com.letraaletra.api.features.items.domain;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;

@JsonTypeName("PERCENTAGE_TIMED")
public record PercentageTimedEffect(
        EffectType type,
        int magnitude,
        int durationMinutes
) implements ItemEffect {
    public PercentageTimedEffect {
        if (type == null) {
            throw new InvalidItemException();
        }

        if (type == EffectType.NICKNAME_CHANGE_GRANT) {
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
