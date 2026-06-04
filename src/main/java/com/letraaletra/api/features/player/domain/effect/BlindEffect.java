package com.letraaletra.api.features.player.domain.effect;

public class BlindEffect implements PlayerEffect {
    private int duration;

    public BlindEffect() {
        this.duration = 6;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void onTurnPassed() {
        duration = Math.max(0, duration - 1);
    }

    @Override
    public boolean canRemove() {
        return duration == 0;
    }
}
