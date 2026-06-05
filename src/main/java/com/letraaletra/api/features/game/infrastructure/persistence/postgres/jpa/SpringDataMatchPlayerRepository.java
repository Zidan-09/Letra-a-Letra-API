package com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataMatchPlayerRepository extends JpaRepository<MatchPlayersJpaEntity, UUID> {
    <S extends MatchPlayersJpaEntity> S save(S entity);
}
