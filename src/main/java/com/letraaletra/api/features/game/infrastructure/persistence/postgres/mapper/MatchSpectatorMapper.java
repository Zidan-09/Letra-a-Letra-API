package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorId;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorsJpaEntity;

import java.util.UUID;

public class MatchSpectatorMapper {
    public static MatchSpectatorsJpaEntity toEntity(Participant participant, UUID matchId) {
        if (participant == null) return null;

        MatchSpectatorsJpaEntity entity = new MatchSpectatorsJpaEntity();

        MatchSpectatorId id = new MatchSpectatorId();
        id.setMatchId(matchId);
        id.setUserId(participant.getUserId());

        entity.setMatchSpectatorId(id);
        entity.setNickname(participant.getNickname());

        return entity;
    }
}
