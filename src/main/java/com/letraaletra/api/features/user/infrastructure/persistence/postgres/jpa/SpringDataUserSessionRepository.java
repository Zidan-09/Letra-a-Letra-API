package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserSessionJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserSessionRepository extends JpaRepository<UserSessionJpaEntity, UUID> {
    Optional<UserSessionJpaEntity> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        FROM UserSessionJpaEntity s
        WHERE s.userId = :userId
    """)
    Optional<UserSessionJpaEntity> findByUserIdForUpdate(@Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        FROM UserSessionJpaEntity s
        WHERE s.currentTokenHash = :tokenHash
            OR s.previousTokenHash = :tokenHash
    """)
    Optional<UserSessionJpaEntity> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);
}
