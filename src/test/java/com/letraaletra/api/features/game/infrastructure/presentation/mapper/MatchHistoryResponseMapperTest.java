package com.letraaletra.api.features.game.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.history.MatchHistory;
import com.letraaletra.api.features.game.domain.history.SpectatorHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.match.MatchHistoryResponseMapper;
import com.letraaletra.api.features.player.domain.PlayerHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchHistoryResponseMapperTest {

    @Test
    @DisplayName("toResponse deve mapear players e spectators")
    void toResponseShouldMapPlayersAndSpectators() {
        UUID pId = UUID.randomUUID();
        UUID sId = UUID.randomUUID();
        Instant now = Instant.now();
        var mh = new MatchHistory(
                List.of(new PlayerHistory(pId, "player", 3, true)),
                List.of(new SpectatorHistory(sId, "spectator")),
                now
        );

        var resp = MatchHistoryResponseMapper.toResponse(mh);

        assertEquals(now, resp.finishedAt());
        assertEquals(1, resp.players().size());
        assertEquals("player", resp.players().getFirst().nickname());
        assertEquals(1, resp.spectators().size());
        assertEquals(sId.toString(), resp.spectators().getFirst().id());
        assertEquals("spectator", resp.spectators().getFirst().nickname());
    }

    @Test
    @DisplayName("toResponse com spectators vazio deve serializar lista vazia")
    void toResponseWithEmptySpectatorsShouldReturnEmptyList() {
        var mh = new MatchHistory(
                List.of(new PlayerHistory(UUID.randomUUID(), "p", 0, false)),
                Instant.now()
        );
        var resp = MatchHistoryResponseMapper.toResponse(mh);
        assertNotNull(resp.spectators());
        assertTrue(resp.spectators().isEmpty());
        assertNotNull(resp.players());
        assertEquals(1, resp.players().size());
    }

    @Test
    @DisplayName("toResponse com múltiplos spectators")
    void toResponseWithMultipleSpectators() {
        var spectators = List.of(
                new SpectatorHistory(UUID.randomUUID(), "s1"),
                new SpectatorHistory(UUID.randomUUID(), "s2"),
                new SpectatorHistory(UUID.randomUUID(), "s3")
        );
        var mh = new MatchHistory(List.of(), spectators, Instant.now());
        var resp = MatchHistoryResponseMapper.toResponse(mh);
        assertEquals(3, resp.spectators().size());
        assertTrue(resp.players().isEmpty());
    }
}
