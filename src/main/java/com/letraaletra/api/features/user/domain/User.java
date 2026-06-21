package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class User {
    private final String id;
    private String nickname;
    private final String email;
    private final String hashPassword;
    private final String googleId;
    private String currentGameId;
    private boolean isAdmin;
    private boolean canChangeNickname;
    private final UserStats stats;
    private List<InventoryItem> inventory;
    private final Wallet wallet;
    private final LocalDateTime createdAt;

    public User(
            String id,
            String nickname,
            String email,
            String hashPassword,
            String googleId,
            boolean isAdmin,
            boolean canChangeNickname,
            UserStats stats,
            List<InventoryItem> inventory,
            Wallet wallet,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.hashPassword = hashPassword;
        this.googleId = googleId;
        this.canChangeNickname = canChangeNickname;
        this.stats = stats;
        this.inventory = inventory;
        this.wallet = wallet;
        this.createdAt = createdAt;
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

    public Wallet getWallet() {
        return wallet;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addToInventory(InventoryItem item) {
        if (item == null) {
            throw new CosmeticNotFoundException();
        }

        if (inventory.stream().anyMatch(cosmetic -> cosmetic.cosmetic_id().equals(item.cosmetic_id()))) {
            throw new InvalidCosmeticException();
        }

        inventory.add(item);
    }

    public void removeFromInventory(String cosmeticId) {
        InventoryItem itemToBeRemoved = inventory.stream()
                .filter(cosmetic -> cosmetic.cosmetic_id().equals(cosmeticId))
                .findFirst().orElseThrow();

        inventory.remove(itemToBeRemoved);

        if (itemToBeRemoved.equipped()) {
            InventoryItem newEquipped = inventory.stream()
                    .filter(cosmetic -> cosmetic.type().equals(itemToBeRemoved.type()))
                    .findFirst().orElseThrow();

            this.equipCosmetic(newEquipped.cosmetic_id());
        }
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
