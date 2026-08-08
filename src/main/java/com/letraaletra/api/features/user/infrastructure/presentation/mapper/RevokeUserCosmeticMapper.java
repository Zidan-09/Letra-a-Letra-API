package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.RevokeUserCosmeticInput;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class RevokeUserCosmeticMapper {
    public static RevokeUserCosmeticInput toInput(AuthenticatedUser principal, UUID userId, UUID cosmeticId) {
        return new RevokeUserCosmeticInput(
                principal,
                userId,
                cosmeticId
        );
    }
}
