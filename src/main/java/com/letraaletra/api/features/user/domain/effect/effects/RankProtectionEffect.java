package com.letraaletra.api.features.user.domain.effect.effects;

import com.letraaletra.api.features.user.domain.effect.UsageBasedEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;

public class RankProtectionEffect implements UsageBasedEffect {
    private int remainingMatches;

    public RankProtectionEffect(int remainingMatches) {
        this(remainingMatches, true);
    }

    public static RankProtectionEffect restore(int remainingMatches) {
        return new RankProtectionEffect(remainingMatches, false);
    }

    private RankProtectionEffect(int remainingMatches, boolean requirePositive) {
        if (remainingMatches < 0 || (requirePositive && remainingMatches == 0)) {
            throw new InvalidUserEffectException();
        }

        this.remainingMatches = remainingMatches;
    }

    @Override
    public int getRemainingUses() {
        return remainingMatches;
    }

    @Override
    public void use() {
        remainingMatches = Math.max(0, remainingMatches - 1);
    }

    @Override
    public boolean canRemove() {
        return remainingMatches == 0;
    }
}
