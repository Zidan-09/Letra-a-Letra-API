package com.letraaletra.api.features.player.domain.effect;

import com.letraaletra.api.features.game.domain.board.position.Position;

import java.util.ArrayList;
import java.util.List;

public class DetectTrapsEffect implements PlayerEffect {
    private int duration;
    private final List<Position> traps;

    public DetectTrapsEffect() {
        this.duration = 10;
        this.traps = new ArrayList<>();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public List<Position> getTraps() {
        return traps;
    }

    public void setTraps(List<Position> trapPositions) {
        traps.addAll(trapPositions);
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
