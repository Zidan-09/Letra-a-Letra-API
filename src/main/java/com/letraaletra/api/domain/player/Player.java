package com.letraaletra.api.domain.player;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String userId;
    private final String nickname;
    private final String avatar;
    private final List<String> inventory = new ArrayList<>();
    private int score = 0;

    public Player(String userId, String nickname, String avatar) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void addToInventory() {
        inventory.add("power");
    }

    public List<String> getInventory() {
        return List.copyOf(inventory);
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }
}
