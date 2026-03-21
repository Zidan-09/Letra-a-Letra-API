package com.letraaletra.api.domain.power.effect;

import com.letraaletra.api.domain.power.Power;

public class EffectPower extends Power {
    int duration;

    public EffectPower(int duration) {
        this.duration = duration;
    }
}
