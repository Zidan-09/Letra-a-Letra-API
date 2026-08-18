package com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InventoryProjection {
    UUID getUserId();

    UUID getCosmeticId();
    String getName();
    CosmeticTypes getType();
    boolean isEquipped();
    LocalDateTime getUnlockedAt();
}