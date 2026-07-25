package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;

import java.util.Optional;
import java.util.UUID;

public interface FindWalletTransaction {
    Optional<WalletTransaction> find(UUID id);
}
