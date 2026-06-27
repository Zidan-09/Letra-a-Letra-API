package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user_inventory\"")
@Getter
@Setter
public class UserInventoryJpaEntity {
    @EmbeddedId
    private UserInventoryId userInventoryId;

    @Column(name = "equipped")
    private boolean isEquipped;

    @Column(name = "unlockedAt", nullable = false)
    private LocalDateTime unlockedAt;
}
