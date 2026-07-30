package com.letraaletra.api.features.admin.domain.permission;

import java.util.*;

public class Permissions {

    private final Map<PermissionKey, Permission> permissions;

    public Permissions() {
        this.permissions = new EnumMap<>(PermissionKey.class);
    }

    public Permission get(PermissionKey key) {
        return permissions.getOrDefault(
                key,
                new Permission(key, Set.of())
        );
    }

    public Collection<Permission> getAll() {
        return List.copyOf(permissions.values());
    }

    public void set(Permission permission) {
        permissions.put(permission.key(), permission);
    }

    public boolean can(PermissionKey key, PermissionAction action) {
        return get(key).can(action);
    }
}