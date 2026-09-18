package com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.ItemKind;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "\"item_definition\"")
@Getter
@Setter
public class ItemJpaEntity {
    @Id
    @Column(name = "item_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false)
    private ItemKind kind;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = true)
    private EquippableCategory category;

    @Column(name = "applicability", nullable = true)
    private String applicability;

    @Column(name = "stackable")
    private boolean stackable;

    @Column(name = "max_stack")
    private Integer maxStack;

    @Column(name = "consumable")
    private boolean consumable;

    @Column(name = "effect")
    private String effect;

    @Column(name = "asset_path")
    private String assetPath;

    @Column(name = "version")
    private int version;

    @Column(name = "available")
    private boolean available;
}
