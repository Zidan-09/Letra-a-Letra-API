package com.letraaletra.api.features.admin.domain.permission;

import com.letraaletra.api.features.admin.domain.exception.AlreadyHaveThisPermissionException;
import com.letraaletra.api.features.admin.domain.exception.InvalidPermissionException;

import java.util.List;
import java.util.UUID;

public class Permissions {
    private List<Permission> permissions;

    public Permissions(
            List<Permission> permissions
    ) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return List.copyOf(permissions);
    }

    private void addPermission(Permission permission) {
        if (permission == null) {
            throw new InvalidPermissionException();
        }

        if (permissions.contains(permission)) {
            throw new AlreadyHaveThisPermissionException();
        }

        permissions.add(permission);
    }

    private void removePermission(UUID permissionId) {
        permissions = permissions.stream().filter(permission ->
                !permission.permissionId().equals(permissionId)
        )
        .toList();
    }
}
