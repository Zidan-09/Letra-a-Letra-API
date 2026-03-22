package com.letraaletra.api.domain.power.effect;

import com.letraaletra.api.domain.power.Power;
import com.letraaletra.api.domain.power.PowerRarity;

public class EffectPower extends Power {
    int duration;

    public EffectPower(String name, PowerRarity rarity, int duration) {
        super(name, rarity);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void decrementDuration() {
        this.duration--;
    }
}
