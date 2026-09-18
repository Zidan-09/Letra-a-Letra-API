package com.letraaletra.api.features.user.domain.effect.effects;

import com.letraaletra.api.features.user.domain.effect.TimeBasedEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;

import java.time.Instant;
import java.util.Objects;

public class RankingPointsBonusEffect implements TimeBasedEffect {
    private final int bonusPercentage;
    private final Instant expiresAt;

    public RankingPointsBonusEffect(int bonusPercentage, Instant expiresAt) {
        if (bonusPercentage <= 0) {
            throw new InvalidUserEffectException();
        }

        this.bonusPercentage = bonusPercentage;
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    }

    public int getBonusPercentage() {
        return bonusPercentage;
    }

    @Override
    public Instant getExpiresAt() {
        return expiresAt;
    }

    @Override
    public boolean canRemove() {
        return isExpired(Instant.now());
    }
}
