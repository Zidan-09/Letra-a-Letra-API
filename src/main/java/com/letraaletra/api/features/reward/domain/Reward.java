package com.letraaletra.api.features.reward.domain;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;

public sealed interface Reward permits SoftCoinsReward, HardGemsReward, CosmeticReward {
    Optional<WalletMovement> apply(User user);
}
