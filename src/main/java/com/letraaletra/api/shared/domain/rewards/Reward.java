package com.letraaletra.api.shared.domain.rewards;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;

public sealed interface Reward permits SoftCoinsReward, HardGemsReward, CosmeticReward {
    Optional<WalletMovement> apply(User user);
}
