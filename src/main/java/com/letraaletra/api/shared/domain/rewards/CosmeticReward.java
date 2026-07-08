package com.letraaletra.api.shared.domain.rewards;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.user.domain.User;

public record CosmeticReward(Cosmetic cosmetic) implements Reward {

    @Override
    public void deliver(User user) {
        user.getInventory().unlock(cosmetic);
    }
}
