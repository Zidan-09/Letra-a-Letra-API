package com.letraaletra.api.shared.application.port;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public interface AdminChecker {
    void check(AuthenticatedUser principal, PermissionKey key, PermissionAction action);
}
