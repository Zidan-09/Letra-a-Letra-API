package com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMatchRepository extends JpaRepository<MatchJpaEntity, UUID> {
    <S extends MatchJpaEntity> S save(S entity);
    List<MatchJpaEntity> findByGameId(UUID gameId);
}
