package com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;

final class ItemEffectCodec {
    private static final String PERCENTAGE_TIMED = "PERCENTAGE_TIMED";
    private static final String NICKNAME_CHANGE = "NICKNAME_CHANGE";

    private ItemEffectCodec() {
    }

    static String encode(ItemEffect effect) {
        if (effect == null) {
            return null;
        }

        if (effect instanceof NicknameChangeEffect) {
            return NICKNAME_CHANGE;
        }

        if (effect instanceof PercentageTimedEffect timed) {
            return PERCENTAGE_TIMED + "|" + timed.type().name()
                    + "|" + timed.magnitude() + "|" + timed.durationMinutes();
        }

        throw new InvalidItemException();
    }

    static ItemEffect decode(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        if (NICKNAME_CHANGE.equals(raw)) {
            return new NicknameChangeEffect();
        }

        String[] parts = raw.split("\\|");

        if (parts.length != 4 || !PERCENTAGE_TIMED.equals(parts[0])) {
            throw new InvalidItemException();
        }

        try {
            return new PercentageTimedEffect(
                    EffectType.valueOf(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidItemException();
        }
    }
}
