package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;

public interface SaveWalletTransaction {
    void save(WalletTransaction walletTransaction);
}
