package com.letraaletra.api.features.offers.domain.rewards;

import com.letraaletra.api.features.user.domain.User;

public sealed interface Reward permits SoftCoinsReward, HardGemsReward, CosmeticReward {
    void deliver(User user);
}
