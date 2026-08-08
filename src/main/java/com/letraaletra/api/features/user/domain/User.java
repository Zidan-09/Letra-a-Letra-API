package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.ban.BanInfo;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyWasBannedException;
import com.letraaletra.api.features.user.domain.exception.UserDoesNotHaveBanException;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private final UUID userId;
    private String username;
    private final String email;
    private String passwordHash;
    private final String googleId;
    private UUID currentGameId;
    private BanInfo banInfo;
    private boolean canChangeNickname;
    private final UserStats stats;
    private final Inventory inventory;
    private final Wallet wallet;
    private final LocalDateTime createdAt;

    private User(
            UUID userId,
            String username,
            String email,
            String passwordHash,
            String googleId,
            UUID currentGameId,
            BanInfo banInfo,
            boolean canChangeNickname,
            UserStats stats,
            Inventory inventory,
            Wallet wallet,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.googleId = googleId;
        this.currentGameId = currentGameId;
        this.banInfo = banInfo;
        this.canChangeNickname = canChangeNickname;
        this.stats = stats;
        this.inventory = inventory;
        this.wallet = wallet;
        this.createdAt = createdAt;
    }

    public static User create(
            String username,
            String email,
            String hashPassword,
            String googleId,
            boolean canChangeNickname
    ) {
        return new User(
                UUID.randomUUID(),
                username,
                email,
                hashPassword,
                googleId,
                null,
                BanInfo.create(),
                canChangeNickname,
                UserStats.create(),
                Inventory.create(),
                Wallet.create(),
                LocalDateTime.now()
        );
    }

    public static User restore(
            UUID userId,
            String username,
            String email,
            String hashPassword,
            String googleId,
            UUID currentGameId,
            boolean canChangeNickname,
            BanInfo banInfo,
            UserStats stats,
            Inventory inventory,
            Wallet wallet,
            LocalDateTime createdAt
    ) {
        return new User(
                userId,
                username,
                email,
                hashPassword,
                googleId,
                currentGameId,
                banInfo,
                canChangeNickname,
                stats,
                inventory,
                wallet,
                createdAt
        );
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getGoogleId() {
        return googleId;
    }

    public boolean isBanned() {
        return banInfo.type() != null;
    }

    public BanInfo getBanInfo() {
        return banInfo;
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

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void ban(LocalDateTime expiresAt, String reason) {
        if (banInfo.type() != null) {
            throw new UserAlreadyWasBannedException();
        }

        banInfo = BanInfo.ban(expiresAt, reason);
    }

    public void unban() {
        if (banInfo.type() == null) {
            throw new UserDoesNotHaveBanException();
        }

        banInfo = null;
    }
}
