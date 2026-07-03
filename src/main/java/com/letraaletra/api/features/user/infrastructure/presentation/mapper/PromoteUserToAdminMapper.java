package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.PromoteUserToAdminInput;

import java.util.UUID;

public class PromoteUserToAdminMapper {
    public static PromoteUserToAdminInput toInput(UUID userId, String userToPromoteId) {
        return new PromoteUserToAdminInput(
                userId,
                UUID.fromString(userToPromoteId)
        );
    }
}
