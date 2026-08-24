package com.letraaletra.api.features.admin.domain.permission;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

import java.util.Set;

public record Permission(
        PermissionKey key,
        Set<PermissionAction> actions
) {
    public Permission {
        actions = Set.copyOf(actions);
    }

    public boolean can(PermissionAction action) {
        return actions.contains(action);
    }
}
