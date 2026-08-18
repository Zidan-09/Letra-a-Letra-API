package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.RevokeUserWalletInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.RevokeUserWalletRequest;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class RevokeUserWalletMapper {
    public static RevokeUserWalletInput toInput(AuthenticatedUser principal, UUID userId, RevokeUserWalletRequest request) {
        return new RevokeUserWalletInput(
                principal,
                userId,
                request.type(),
                request.amount()
        );
    }
}
