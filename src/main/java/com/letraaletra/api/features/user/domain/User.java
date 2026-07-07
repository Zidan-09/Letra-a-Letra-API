package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private final UUID id;
    private String nickname;
    private final String email;
    private final String hashPassword;
    private final String googleId;
    private UUID currentGameId;
    private boolean canChangeNickname;
    private final UserStats stats;
    private final Inventory inventory;
    private final Wallet wallet;
    private final LocalDateTime createdAt;

    public User(
            UUID id,
            String nickname,
            String email,
            String hashPassword,
            String googleId,
            boolean canChangeNickname,
            UserStats stats,
            Inventory inventory,
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

    public UUID getId() {
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

    public Inventory getInventory() {
        return inventory;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isNotInGame() {
        return currentGameId == null;
    }

    public UUID getCurrentGameId() {
        return currentGameId;
    }

    public void enterGame(UUID gameId) {
        if (gameId == null || gameId.toString().isBlank()) {
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
}
