package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemId;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.projection.UserItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataUserItemRepository extends JpaRepository<UserItemJpaEntity, UserItemId> {

    @Query("""
        SELECT
            i.userItemId.userId AS userId,
            i.userItemId.itemId AS itemId,
            i.quantity AS quantity,
            i.equipped AS equipped,
            i.acquiredAt AS acquiredAt,
            i.expiresAt AS expiresAt

        FROM UserItemJpaEntity i

        WHERE i.userItemId.userId = :ownerId
    """)
    List<UserItemProjection> findItemsByOwner(
            @Param("ownerId") UUID ownerId
    );

    @Modifying
    @Query("""
    DELETE FROM UserItemJpaEntity i
        WHERE i.userItemId.userId = :ownerId
    """)
    void deleteItemsByOwner(@Param("ownerId") UUID ownerId);
}
