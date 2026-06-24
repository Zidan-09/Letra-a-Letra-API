package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatchMapper {
    public static MatchJpaEntity toEntity(GameState domain, UUID gameId, LocalDateTime endedAt) {
        if (domain == null) return null;

        MatchJpaEntity entity = new MatchJpaEntity();

        entity.setId(domain.getMatchId());
        entity.setGameId(gameId);
        entity.setGameMode(domain.getBoard().gameMode());
        entity.setEndedAt(endedAt);

        return entity;
    }
}
