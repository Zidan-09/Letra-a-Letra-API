package com.letraaletra.api.features.reward.domain;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.Optional;
import java.util.UUID;

public record ItemGrantReward(UUID definitionId, int quantity) implements Reward {

    @Override
    public Optional<WalletMovement> apply(User user) {
        return Optional.empty();
    }
}
