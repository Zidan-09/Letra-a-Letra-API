package com.letraaletra.api.features.player.domain.effect;

public interface PlayerEffect {
    int getDuration();
    void onTurnPassed();
    boolean canRemove();
}
