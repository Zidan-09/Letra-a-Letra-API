package com.letraaletra.api.features.items.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataItemRepository extends JpaRepository<ItemJpaEntity, UUID> {
    Optional<ItemJpaEntity> findByName(String name);

    @Query("SELECT e FROM ItemJpaEntity e " +
            "WHERE (:kind IS NULL OR e.kind = :kind) " +
            "AND (:category IS NULL OR e.category = :category) " +
            "AND (:available IS NULL OR e.available = :available)")
    Page<ItemJpaEntity> search(
            @Param("kind") ItemKind kind,
            @Param("category") EquippableCategory category,
            @Param("available") Boolean available,
            Pageable pageable
    );
}
