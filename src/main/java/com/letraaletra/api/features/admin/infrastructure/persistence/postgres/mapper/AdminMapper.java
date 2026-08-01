package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.permission.Permissions;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminJpaEntity;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPermissionId;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPermissionJpaEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminMapper {
    public static Admin toDomain(AdminJpaEntity entity, List<AdminPermissionJpaEntity> permissionEntities) {
        Permissions permissions = new Permissions();

        Map<PermissionKey, Set<PermissionAction>> grouped = permissionEntities.stream()
                .collect(Collectors.groupingBy(
                        permission -> permission.getId().getPermissionKey(),
                        Collectors.mapping(
                                permission -> permission.getId().getAction(),
                                Collectors.toSet()
                        )
                ));

        grouped.forEach((key, actions) ->
                permissions.set(new Permission(key, actions))
        );
        return Admin.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getHashPassword(),
                entity.isSuper(),
                permissions,
                entity.getCreatedAt()
        );
    }

    public static AdminJpaEntity toEntity(Admin domain) {
        AdminJpaEntity entity = new AdminJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setSuper(domain.isSuper());
        entity.setHashPassword(domain.getHashPassword());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }

    public static List<AdminPermissionJpaEntity> toPermissionEntities(Admin admin) {
        return admin.getPermissions()
                .getAll()
                .stream()
                .flatMap(permission ->
                        permission.actions()
                                .stream()
                                .map(action -> {
                                    AdminPermissionJpaEntity entity = new AdminPermissionJpaEntity();

                                    AdminPermissionId adminPermissionId = new AdminPermissionId();

                                    adminPermissionId.setAdminId(admin.getId());
                                    adminPermissionId.setPermissionKey(permission.key());
                                    adminPermissionId.setAction(action);

                                    entity.setId(adminPermissionId);

                                    return entity;
                                })
                )
                .toList();
    }
}
