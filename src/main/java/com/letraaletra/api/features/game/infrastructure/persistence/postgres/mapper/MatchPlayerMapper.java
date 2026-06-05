package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayerId;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;

import java.util.UUID;

public class MatchPlayerMapper {
    public static MatchPlayersJpaEntity toEntity(Player domain, String matchId) {
        if (domain == null) return null;

        MatchPlayersJpaEntity entity = new MatchPlayersJpaEntity();

        MatchPlayerId id = new MatchPlayerId();

        id.setMatchId(UUID.fromString(matchId));
        id.setUserId(UUID.fromString(domain.getUserId()));

        entity.setMatchPlayerId(id);
        entity.setScore(domain.getScore());
        entity.setWinner(domain.getScore() == 3);

        return entity;
    }
}
