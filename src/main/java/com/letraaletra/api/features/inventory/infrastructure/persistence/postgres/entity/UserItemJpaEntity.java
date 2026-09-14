package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user_item\"")
@Getter
@Setter
public class UserItemJpaEntity {
    @EmbeddedId
    private UserItemId userItemId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "equipped")
    private boolean equipped;

    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
