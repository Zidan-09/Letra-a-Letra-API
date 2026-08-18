package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "\"cosmetic\"")
@Getter
@Setter
public class CosmeticJpaEntity {
    @Id
    @Column(name = "cosmeticId", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CosmeticTypes type;

    @Column(name = "asset_path")
    private String assetPath;

    @Column(name = "version")
    private int version;

    @Column(name = "available")
    private boolean available;
}