package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.RevokeAdminRoleInput;

import java.util.UUID;

public class RevokeAdminRoleMapper {
    public static RevokeAdminRoleInput toInput(UUID userId, String userToRevoke) {
        return new RevokeAdminRoleInput(
                userId,
                UUID.fromString(userToRevoke)
        );
    }
}
