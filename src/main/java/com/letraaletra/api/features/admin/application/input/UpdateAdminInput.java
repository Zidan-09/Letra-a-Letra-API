package com.letraaletra.api.features.admin.application.input;

import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.List;
import java.util.UUID;

public record UpdateAdminInput(
        AuthenticatedUser principal,
        UUID adminId,
        String name,
        String email,
        List<Permission> permissions
) {
}
