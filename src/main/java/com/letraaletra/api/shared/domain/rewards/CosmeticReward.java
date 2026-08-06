package com.letraaletra.api.shared.domain.rewards;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;

public record CosmeticReward(Cosmetic cosmetic) implements Reward {

    @Override
    public Optional<WalletMovement> apply(User user) {
        user.getInventory().unlock(cosmetic);

        return Optional.empty();
    }
}
