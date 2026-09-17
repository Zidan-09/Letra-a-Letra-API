package com.letraaletra.api.features.player.domain;

import com.letraaletra.api.features.player.domain.effect.PlayerActiveEffects;
import com.letraaletra.api.features.player.domain.inventory.PlayerInventory;

import java.util.*;

public class Player {
    private final UUID userId;
    private final String nickname;
    private final PlayerInventory inventory;
    private int score = 0;
    private final PlayerActiveEffects activeEffects;
    private int passedTurn = 0;

    public Player(
            UUID userId,
            String nickname,
            PlayerInventory inventory,
            PlayerActiveEffects activeEffects
    ) {
        this.userId = Objects.requireNonNull(userId);
        this.nickname = Objects.requireNonNull(nickname);
        this.inventory = Objects.requireNonNull(inventory);
        this.activeEffects = Objects.requireNonNull(activeEffects);
    }

    public static Player create(
            UUID userId,
            String nickname
    ) {
        return new Player(
                userId,
                nickname,
                PlayerInventory.create(),
                PlayerActiveEffects.create()
        );
    }

    public PlayerActiveEffects getActiveEffects() {
        return activeEffects;
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

    public int getPassedTurn() {
        return passedTurn;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }

    public boolean canNotPlay() {
        return activeEffects.isFrozen() && !inventory.hasFreezeDefense();
    }

    public void passedTurn() {
        passedTurn++;
    }

    public void resetPassedTurn() {
        passedTurn = 0;
    }
}
