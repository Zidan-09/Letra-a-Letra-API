package com.letraaletra.api.features.offers.domain.rewards;

import com.letraaletra.api.features.user.domain.User;

public record HardGemsReward(int amount) implements Reward {

    @Override
    public void deliver(User user) {
        user.getWallet().addHard(amount);
    }
}
