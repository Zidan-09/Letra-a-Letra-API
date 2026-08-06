package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository
        extends JpaRepository<UserJpaEntity, UUID> {

    String FIND_DETAILS = """
    SELECT
        u.id AS userId,
        u.username AS username,
        u.email AS email,
        u.passwordHash AS passwordHash,
        u.googleId AS googleId,

        u.currentGameId AS currentGameId,
        u.banned AS banned,
        u.canChangeNickname AS canChangeNickname,

        s.totalMatches AS totalMatches,
        s.totalWins AS totalWins,
        s.winStreak AS winStreak,
        s.level AS level,
        s.experience AS experience,
        s.rankingPoints AS rankingPoints,

        w.softCoins AS softCoins,
        w.hardGems AS hardGems,

        u.createdAt AS createdAt

    FROM UserJpaEntity u

    JOIN UserStatsJpaEntity s
        ON s.userId = u.id

    JOIN UserWalletJpaEntity w
        ON w.userId = u.id
    """;

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query(FIND_DETAILS + " WHERE u.id = :id")
    Optional<UserProjection> findDetailsById(UUID id);

    @Query(FIND_DETAILS + " WHERE u.id IN :ids")
    List<UserProjection> findDetailsByIds(List<UUID> ids);

    @Query(FIND_DETAILS + " WHERE u.username = :username")
    Optional<UserProjection> findDetailsByUsername(String username);

    @Query(FIND_DETAILS + " WHERE u.email = :email")
    Optional<UserProjection> findDetailsByEmail(String email);

    @Query(FIND_DETAILS + " WHERE u.googleId = :googleId")
    Optional<UserProjection> findDetailsByGoogleId(String googleId);

    @Query(FIND_DETAILS)
    Page<UserProjection> findDetails(Pageable pageable);
}
