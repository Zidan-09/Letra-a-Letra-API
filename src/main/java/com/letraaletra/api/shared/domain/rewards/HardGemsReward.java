package com.letraaletra.api.shared.domain.rewards;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;

public record HardGemsReward(int amount) implements Reward {

    @Override
    public Optional<WalletMovement> apply(User user) {
        return Optional.of(
                user.getWallet().add(CoinType.HARD, amount)
        );
    }
}
