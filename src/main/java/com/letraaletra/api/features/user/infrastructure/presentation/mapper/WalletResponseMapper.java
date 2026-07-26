package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.WalletResponse;

public class WalletResponseMapper {
    public static WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getBalance().coins(),
                wallet.getBalance().gems()
        );
    }
}
