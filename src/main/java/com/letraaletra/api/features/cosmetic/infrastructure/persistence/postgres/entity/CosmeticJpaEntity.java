package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"cosmetic\"")
@Getter
@Setter
public class CosmeticJpaEntity {
    @Id
    @Column(name = "cosmetic_id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CosmeticTypes type;

    @Column(name = "asset_path")
    private String assetPath;

    @Column(name = "version")
    private int version;
}