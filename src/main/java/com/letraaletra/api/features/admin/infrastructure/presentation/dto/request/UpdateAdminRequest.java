package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.admin.domain.permission.Permission;

import java.util.List;

public record UpdateAdminRequest(
        String name,
        String email,
        List<Permission> permissions
) {
}
