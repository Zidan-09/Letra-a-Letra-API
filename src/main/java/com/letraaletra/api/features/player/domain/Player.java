package com.letraaletra.api.features.player.domain;

import com.letraaletra.api.features.player.domain.effect.FreezeEffect;
import com.letraaletra.api.features.player.domain.effect.PlayerEffect;
import com.letraaletra.api.features.player.domain.inventory.PlayerInventory;

import java.util.*;

public class Player {
    private final UUID userId;
    private final String nickname;
    private final PlayerInventory inventory;
    private int score = 0;
    private final List<PlayerEffect> effects = new ArrayList<>();
    private int passedTurn = 0;

    public Player(UUID userId, String nickname, PlayerInventory inventory) {
        this.userId = Objects.requireNonNull(userId);
        this.nickname = Objects.requireNonNull(nickname);
        this.inventory = Objects.requireNonNull(inventory);
    }

    public static Player create(
            UUID userId,
            String nickname
    ) {
        return new Player(
                userId,
                nickname,
                PlayerInventory.create()
        );
    }

    public boolean isFrozen() {
        return effects.stream().anyMatch(effect -> effect instanceof FreezeEffect);
    }

    public UUID getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public List<PlayerEffect> getEffects() {
        return List.copyOf(effects);
    }

    public int getPassedTurn() {
        return passedTurn;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }

    public void applyEffect(PlayerEffect effect) {
        effects.add(effect);
    }

    public void decrementEffectDuration() {
        effects.forEach(PlayerEffect::onTurnPassed);
        effects.removeIf(PlayerEffect::canRemove);
    }

    public void removeEffect(Class<? extends PlayerEffect> type) {
        effects.removeIf(type::isInstance);
    }

    public boolean canNotPlay() {
        return isFrozen() && !inventory.hasFreezeDefense();
    }

    public void passedTurn() {
        passedTurn++;
    }

    public void resetPassedTurn() {
        passedTurn = 0;
    }
}
