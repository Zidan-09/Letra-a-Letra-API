package com.letraaletra.api.domain.player;

import com.letraaletra.api.domain.power.Power;
import com.letraaletra.api.domain.power.effect.EffectPower;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String playerId;
    private final int turn;
    private int score = 0;
    private boolean hasEffect;
    private EffectPower activeEffect;
    private final List<Power> inventory = new ArrayList<>(5);

    public Player(String playerId, int turn) {
        this.playerId = playerId;
        this.turn = turn;
        this.hasEffect = false;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getTurn() {
        return turn;
    }

    public int getScore() {
        return score;
    }

    public boolean isHasEffect() {
        return hasEffect;
    }

    public EffectPower getActiveEffect() {
        return activeEffect;
    }

    public List<Power> getInventory() {
        return List.copyOf(inventory);
    }

    public void incrementScore() {
        this.score++;
    }

    public void applyEffectOnPlayer(EffectPower power) {
        this.hasEffect = true;
        this.activeEffect = power;
    }

    public void decrementEffectDuration() {
        activeEffect.decrementDuration();

        if (activeEffect.getDuration() <= 0) {
            removeEffectOfPlayer();
        }
    }

    public void removeEffectOfPlayer() {
        this.hasEffect = false;
        this.activeEffect = null;
    }

    public void addPowerOnInventory(Power power) {
        inventory.add(power);
    }

    public void removePowerFromInventory(int index) {
        inventory.remove(index);
    }
}
