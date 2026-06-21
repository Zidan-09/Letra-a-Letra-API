package com.letraaletra.api.features.user.domain.factory;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserFactory {
    public User createLocal(String nickname, String email, String passwordHash) {
        return new User(
                UUID.randomUUID().toString(),
                nickname,
                email,
                passwordHash,
                null,
                false,
                true,
                getInitialStats(),
                getInitialCosmetics(),
                getInitialWallet()
        );
    }

    public User createGoogle(String email, String googleId) {
        return new User(
                UUID.randomUUID().toString(),
                null,
                email,
                null,
                googleId,
                false,
                true,
                getInitialStats(),
                getInitialCosmetics(),
                getInitialWallet()
        );
    }

    private UserStats getInitialStats() {
        return new UserStats(0, 0, 0, 1, 0, 0);
    }

    private List<InventoryItem> getInitialCosmetics() {
        LocalDateTime now = LocalDateTime.now();

//        return List.of(
//                new InventoryItem("old-man-avatar-free", "old-man", CosmeticTypes.AVATAR, true, now),
//                new InventoryItem("little-girl-avatar-free", "little-girl", CosmeticTypes.AVATAR, false, now)
//        );

        return List.of();
    }

    private Wallet getInitialWallet() {
        return new Wallet(0, 0);
    }
}