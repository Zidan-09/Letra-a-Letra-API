package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "\"admin_permission\"")
public class AdminPermissionJpaEntity {

    @EmbeddedId
    private AdminPermissionId id;
}
