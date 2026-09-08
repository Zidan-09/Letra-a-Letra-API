package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"level\"", uniqueConstraints = @UniqueConstraint(columnNames = "level"))
public class LevelJpaEntity {
    @Id
    @Column(name = "level_id")
    private UUID id;

    @Column(name = "level", nullable = false)
    private Integer level;
}
