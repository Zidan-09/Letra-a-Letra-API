package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCosmeticRepository extends JpaRepository<CosmeticJpaEntity, UUID> {
    Optional<CosmeticJpaEntity> findByName(String name);

    @Query("""
    SELECT c
    FROM CosmeticJpaEntity c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
    ORDER BY
    CASE
        WHEN LOWER(c.name) LIKE LOWER(CONCAT(:search, '%')) THEN 0
        ELSE 1
    END,
    c.name
""")
    Page<CosmeticJpaEntity> search(
            @Param("search") String search,
            Pageable pageable
    );
}