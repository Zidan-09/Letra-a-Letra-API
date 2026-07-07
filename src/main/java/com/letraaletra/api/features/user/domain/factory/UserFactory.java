package com.letraaletra.api.features.user.domain.factory;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class UserFactory {
    public User createLocal(String nickname, String email, String passwordHash) {
        return new User(
                UUID.randomUUID(),
                nickname,
                email,
                passwordHash,
                null,
                true,
                getInitialStats(),
                getInitialCosmetics(),
                getInitialWallet(),
                LocalDateTime.now()
        );
    }

    public User createGoogle(String email, String googleId) {
        return new User(
                UUID.randomUUID(),
                null,
                email,
                null,
                googleId,
                true,
                getInitialStats(),
                getInitialCosmetics(),
                getInitialWallet(),
                LocalDateTime.now()
        );
    }

    private UserStats getInitialStats() {
        return new UserStats(0, 0, 0, 1, 0, 0);
    }

    private Inventory getInitialCosmetics() {
        return new Inventory(new ArrayList<>());
    }

    private Wallet getInitialWallet() {
        return new Wallet(0, 0);
    }
}