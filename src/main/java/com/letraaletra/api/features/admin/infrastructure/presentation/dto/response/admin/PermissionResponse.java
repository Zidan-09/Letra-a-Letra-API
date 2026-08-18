package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;

import java.util.Set;

public record PermissionResponse(
        PermissionKey key,
        Set<PermissionAction> actions
) {
}
