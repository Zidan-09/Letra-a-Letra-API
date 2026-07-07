package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.permission.Permissions;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminJpaEntity;

import java.util.List;

public class AdminMapper {
    public static Admin toDomain(AdminJpaEntity entity) {
        return Admin.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getHashPassword(),
                entity.getCreatedAt()
        );
    }

    public static AdminJpaEntity toEntity(Admin domain) {
        AdminJpaEntity entity = new AdminJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setHashPassword(domain.getHashPassword());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}
