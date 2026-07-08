package com.letraaletra.api.features.offers.domain.rewards;

import com.letraaletra.api.features.user.domain.User;

public record SoftCoinsReward(int amount) implements Reward {

    @Override
    public void deliver(User user) {
        user.getWallet().addSoft(amount);
    }
}
