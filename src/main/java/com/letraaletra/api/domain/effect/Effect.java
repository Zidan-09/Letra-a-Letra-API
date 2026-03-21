package com.letraaletra.api.domain.effect;

import com.letraaletra.api.domain.power.effect.EffectPower;

public class Effect {
    private final EffectPower power;
    private int remaining;

    public Effect(EffectPower power, int remaining) {
        this.power = power;
        this.remaining = remaining;
    }

    public int getRemaining() {
        return remaining;
    }

    public EffectPower getPower() {
        return power;
    }

    public void decrementDuration() {
        this.remaining = this.remaining--;
    }
}
