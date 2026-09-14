package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserItemProjection {
    UUID getUserId();
    UUID getItemId();
    int getQuantity();
    boolean isEquipped();
    LocalDateTime getAcquiredAt();
    LocalDateTime getExpiresAt();
}
