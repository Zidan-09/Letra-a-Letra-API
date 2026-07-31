package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class AdminPermissionId implements Serializable {
    private UUID adminId;

    @Enumerated(EnumType.STRING)
    private PermissionKey permissionKey;

    @Enumerated(EnumType.STRING)
    private PermissionAction action;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminPermissionId that)) return false;
        return Objects.equals(adminId, that.adminId) &&
                Objects.equals(permissionKey, that.permissionKey) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId, permissionKey, action);
    }
}
