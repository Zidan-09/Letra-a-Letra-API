package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.stats.UserStats;

import java.util.List;

public class User {
    private final String id;
    private String nickname;
    private final String email;
    private final String hashPassword;
    private final String googleId;
    private String currentGameId;
    private boolean canChangeNickname;
    private final UserStats stats;
    private List<InventoryItem> inventory;

    public User(
            String id,
            String nickname,
            String email,
            String hashPassword,
            String googleId,
            boolean canChangeNickname,
            UserStats stats,
            List<InventoryItem> inventory
    ) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.hashPassword = hashPassword;
        this.googleId = googleId;
        this.canChangeNickname = canChangeNickname;
        this.stats = stats;
        this.inventory = inventory;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getGoogleId() {
        return googleId;
    }

    public boolean canChangeNickname() {
        return canChangeNickname;
    }

    public UserStats getStats() {
        return stats;
    }

    public List<InventoryItem> getInventory() {
        return List.copyOf(inventory);
    }

    public void setInventory(List<InventoryItem> newInventory) {
        inventory = newInventory;
    }

    public boolean isNotInGame() {
        return currentGameId == null;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void enterGame(String gameId) {
        if (gameId == null || gameId.isBlank()) {
            throw new GameNotFoundException();
        }

        if (this.currentGameId != null) {
            throw new UserAlreadyInGameException();
        }

        this.currentGameId = gameId;
    }

    public void leaveGame() {
        this.currentGameId = null;
    }

    public void registerMatchResult(boolean isWinner) {
        if (isWinner) {
            stats.registerWin();
        } else {
            stats.registerLose();
        }
    }

    public void setCanChangeNickname(boolean canChangeNickname) {
        this.canChangeNickname = canChangeNickname;
    }

    public void equipCosmetic(String cosmeticId) {
        InventoryItem targetItem = this.inventory.stream()
                .filter(item -> item.cosmetic_id().equals(cosmeticId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui este cosmético."));

        this.inventory = this.inventory.stream()
                .map(item -> {
                    if (item.type() == targetItem.type()) {
                        boolean isTarget = item.cosmetic_id().equals(cosmeticId);
                        return new InventoryItem(item.cosmetic_id(), item.name(), item.type(), isTarget, item.unlocked_at());
                    }

                    return item;
                })
                .toList();
    }
}
