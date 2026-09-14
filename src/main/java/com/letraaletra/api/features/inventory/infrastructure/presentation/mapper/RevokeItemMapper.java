package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.RevokeItemInput;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class RevokeItemMapper {
    public static RevokeItemInput toInput(AuthenticatedUser principal, UUID itemId) {
        return new RevokeItemInput(
                principal,
                principal.auth(),
                itemId
        );
    }
}
