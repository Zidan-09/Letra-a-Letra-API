package com.letraaletra.api.features.reward.domain;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.InventoryMovement;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.List;
import java.util.Optional;

public record CosmeticReward(Cosmetic cosmetic) implements Reward {

    @Override
    public Optional<WalletMovement> apply(User user) {
        return Optional.empty();
    }

    @Override
    public List<InventoryMovement> applyInventory(User user) {
        return user.getInventory().unlock(cosmetic);
    }
}
