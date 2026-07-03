package com.letraaletra.api.features.user.application.input;

import java.util.UUID;

public record RevokeAdminRoleInput(
        UUID userId,
        UUID userToRevokeId
) {
}
