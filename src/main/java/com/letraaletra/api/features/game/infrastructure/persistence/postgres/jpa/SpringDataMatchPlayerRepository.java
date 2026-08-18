package com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayerId;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMatchPlayerRepository
        extends JpaRepository<MatchPlayersJpaEntity, MatchPlayerId> {

    List<MatchPlayersJpaEntity> findByMatchPlayerIdMatchIdIn(List<UUID> matchIds);
}