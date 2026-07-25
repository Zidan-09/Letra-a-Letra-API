package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;

import java.util.List;
import java.util.UUID;

public interface GetWalletTransactions {
    List<WalletTransaction> get(Object object);
    List<WalletTransaction> getByUserId(UUID userId);
}
