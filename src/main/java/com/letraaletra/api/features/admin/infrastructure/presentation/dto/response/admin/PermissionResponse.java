package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

import java.util.Set;

public record PermissionResponse(
        PermissionKey key,
        Set<PermissionAction> actions
) {
}
