package com.letraaletra.api.domain.game.player;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String userId;
    private final List<String> inventory = new ArrayList<>();
    private int score = 0;

    public Player(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void addToInventory() {
        if (inventory.size() == 5) return;

        inventory.add("power");
    }

    public List<String> getInventory() {
        return List.copyOf(inventory);
    }

    public void removeFromInventory(int index) {
        inventory.remove(index);
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }
}
