package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryId;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataUserInventoryRepository extends JpaRepository<UserInventoryJpaEntity, UserInventoryId> {
    @Query("""
        SELECT new com.letraaletra.api.features.user.domain.inventory.InventoryItem(
            ui.userInventoryId.cosmeticId,
            c.name,
            c.type,
            ui.isEquipped,
            ui.unlockedAt
        )
        FROM UserInventoryJpaEntity ui
        JOIN CosmeticJpaEntity c ON ui.userInventoryId.cosmeticId = c.id
        WHERE ui.userInventoryId.userId = :userId
    """)
    List<InventoryItem> findInventoryItemsByUserId(@Param("userId") UUID userId);
}