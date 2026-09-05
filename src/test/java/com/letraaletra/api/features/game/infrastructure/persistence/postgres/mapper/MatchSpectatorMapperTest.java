package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorsJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchSpectatorMapperTest {

    @Test
    @DisplayName("toEntity deve mapear Participant SPECTATOR para entidade")
    void toEntityShouldMapParticipant() {
        UUID userId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Participant p = new Participant(userId, "socket-1", "nick1", List.of());
        p.changeRole(ParticipantRole.SPECTATOR);

        MatchSpectatorsJpaEntity entity = MatchSpectatorMapper.toEntity(p, matchId);

        assertNotNull(entity);
        assertEquals(matchId, entity.getMatchSpectatorId().getMatchId());
        assertEquals(userId, entity.getMatchSpectatorId().getUserId());
        assertEquals("nick1", entity.getNickname());
    }

    @Test
    @DisplayName("toEntity com null deve retornar null")
    void toEntityWithNullShouldReturnNull() {
        assertNull(MatchSpectatorMapper.toEntity(null, UUID.randomUUID()));
    }

    @Test
    @DisplayName("toEntity deve preservar nickname mesmo com role PLAYER (mapper não valida papel)")
    void toEntityPreservesNickname() {
        UUID userId = UUID.randomUUID();
        Participant p = new Participant(userId, "s", "playerNick", List.of());
        p.changeRole(ParticipantRole.PLAYER);
        var e = MatchSpectatorMapper.toEntity(p, UUID.randomUUID());
        assertEquals("playerNick", e.getNickname());
    }
}
