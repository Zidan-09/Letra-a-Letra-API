package com.letraaletra.api.features.reward.domain;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.reward.domain.exception.InvalidRewardQuantityException;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;

public record SoftCoinsReward(int amount) implements Reward {

    public SoftCoinsReward {
        if (amount <= 0) {
            throw new InvalidRewardQuantityException();
        }
    }

    @Override
    public Optional<WalletMovement> apply(User user) {
        return Optional.of(user.getWallet().add(CoinType.SOFT, amount));
    }
}
