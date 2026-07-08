package com.letraaletra.api.shared.domain.rewards;

import com.letraaletra.api.features.user.domain.User;

public record HardGemsReward(int amount) implements Reward {

    @Override
    public void deliver(User user) {
        user.getWallet().addHard(amount);
    }
}
