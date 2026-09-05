package com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorId;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMatchSpectatorRepository
        extends JpaRepository<MatchSpectatorsJpaEntity, MatchSpectatorId> {

    List<MatchSpectatorsJpaEntity> findByMatchSpectatorIdMatchIdIn(List<UUID> matchIds);
}
