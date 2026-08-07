package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryId;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.InventoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataUserInventoryRepository
        extends JpaRepository<UserInventoryJpaEntity, UserInventoryId> {

    @Query("""
        SELECT
            i.userInventoryId.userId AS userId,
            c.id AS cosmeticId,
            c.name AS name,
            c.type AS type,
            i.equipped AS equipped,
            i.unlockedAt AS unlockedAt

        FROM UserInventoryJpaEntity i

        JOIN CosmeticJpaEntity c
            ON c.id = i.userInventoryId.cosmeticId

        WHERE i.userInventoryId.userId IN :userIds
    """)
    List<InventoryProjection> findInventoryByUserIds(
            @Param("userIds") List<UUID> userIds
    );


    @Query("""
        SELECT
            i.userInventoryId.userId AS userId,
            c.id AS cosmeticId,
            c.name AS name,
            c.type AS type,
            i.equipped AS equipped,
            i.unlockedAt AS unlockedAt

        FROM UserInventoryJpaEntity i

        JOIN CosmeticJpaEntity c
            ON c.id = i.userInventoryId.cosmeticId

        WHERE i.userInventoryId.userId = :userId
    """)
    List<InventoryProjection> findInventory(
            @Param("userId") UUID userId
    );

    @Query(
            value = """
        SELECT
            i.userInventoryId.userId AS userId,
            c.id AS cosmeticId,
            c.name AS name,
            c.type AS type,
            i.equipped AS equipped,
            i.unlockedAt AS unlockedAt

        FROM UserInventoryJpaEntity i

        JOIN CosmeticJpaEntity c
            ON c.id = i.userInventoryId.cosmeticId

        WHERE i.userInventoryId.userId = :userId
    """,
            countQuery = """
        SELECT COUNT(i)

        FROM UserInventoryJpaEntity i

        WHERE i.userInventoryId.userId = :userId
    """
    )
    Page<InventoryProjection> findInventoryPage(
            @Param("userId") UUID userId,
            Pageable pageable
    );

    @Modifying
    @Query("""
    DELETE FROM UserInventoryJpaEntity i
        WHERE i.userInventoryId.userId = :userId
    """)
    void deleteAllByUserId(UUID userId);
}