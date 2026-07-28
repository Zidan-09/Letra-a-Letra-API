package com.letraaletra.api.shared.domain.rewards;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;

public record SoftCoinsReward(int amount) implements Reward {

    @Override
    public Optional<WalletMovement> deliver(User user) {
        Balance balanceBefore = user.getWallet().getBalance();

        user.getWallet().addSoft(amount);

        return Optional.of(
                new WalletMovement(
                        CoinType.SOFT,
                        balanceBefore,
                        amount,
                        OperationType.CREDIT
                )
        );
    }
}
