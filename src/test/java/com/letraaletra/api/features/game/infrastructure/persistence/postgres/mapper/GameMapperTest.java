package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.history.GameHistory;
import com.letraaletra.api.features.game.domain.history.SpectatorHistory;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.GameJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayerId;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorId;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorsJpaEntity;
import com.letraaletra.api.features.player.domain.PlayerHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    @Test
    @DisplayName("toDomain deve mapear players e spectators corretamente")
    void toDomainShouldMapPlayersAndSpectators() {
        UUID gameId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID s1 = UUID.randomUUID();

        GameJpaEntity game = new GameJpaEntity();
        game.setId(gameId);
        game.setRoomName("room");
        game.setGameType(GameType.CUSTOM);
        game.setStatus(GameStatus.CLOSED);
        game.setRoomCode("ABC123");

        MatchJpaEntity match = new MatchJpaEntity();
        match.setId(matchId);
        match.setGameId(gameId);
        match.setEndedAt(LocalDateTime.now());

        MatchPlayersJpaEntity mp1 = new MatchPlayersJpaEntity();
        MatchPlayerId mpId1 = new MatchPlayerId(); mpId1.setMatchId(matchId); mpId1.setUserId(p1);
        mp1.setMatchPlayerId(mpId1); mp1.setNickname("player1"); mp1.setScore(3); mp1.setWinner(true);

        MatchPlayersJpaEntity mp2 = new MatchPlayersJpaEntity();
        MatchPlayerId mpId2 = new MatchPlayerId(); mpId2.setMatchId(matchId); mpId2.setUserId(p2);
        mp2.setMatchPlayerId(mpId2); mp2.setNickname("player2"); mp2.setScore(1); mp2.setWinner(false);

        MatchSpectatorsJpaEntity sp1 = new MatchSpectatorsJpaEntity();
        MatchSpectatorId spId1 = new MatchSpectatorId(); spId1.setMatchId(matchId); spId1.setUserId(s1);
        sp1.setMatchSpectatorId(spId1); sp1.setNickname("spec1");

        Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch = Map.of(matchId, List.of(mp1, mp2));
        Map<UUID, List<MatchSpectatorsJpaEntity>> spectatorsByMatch = Map.of(matchId, List.of(sp1));

        GameHistory history = GameMapper.toDomain(game, List.of(match), playersByMatch, spectatorsByMatch);

        assertEquals(gameId, history.roomId());
        assertEquals(1, history.matches().size());
        assertEquals(2, history.matches().getFirst().players().size());
        assertEquals(1, history.matches().getFirst().spectators().size());
        assertEquals("player1", history.matches().getFirst().players().getFirst().nickname());
        SpectatorHistory sh = history.matches().getFirst().spectators().getFirst();
        assertEquals(s1, sh.spectatorId());
        assertEquals("spec1", sh.nickname());
    }

    @Test
    @DisplayName("toDomain com lista vazia de spectators deve retornar lista vazia (compat com partidas antigas)")
    void toDomainWithEmptySpectatorsShouldReturnEmptyList() {
        UUID gameId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();

        GameJpaEntity game = new GameJpaEntity();
        game.setId(gameId);
        game.setRoomName("room2");
        game.setGameType(GameType.RANKING);
        game.setStatus(GameStatus.RUNNING);
        game.setRoomCode("XYZ");

        MatchJpaEntity match = new MatchJpaEntity();
        match.setId(matchId);
        match.setGameId(gameId);
        match.setEndedAt(null);

        MatchPlayersJpaEntity mp = new MatchPlayersJpaEntity();
        MatchPlayerId mpId = new MatchPlayerId(); mpId.setMatchId(matchId); mpId.setUserId(p1);
        mp.setMatchPlayerId(mpId); mp.setNickname("p"); mp.setScore(0); mp.setWinner(false);

        Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch = Map.of(matchId, List.of(mp));

        // legacy overload without spectators
        GameHistory historyLegacy = GameMapper.toDomain(game, List.of(match), playersByMatch);
        assertEquals(0, historyLegacy.matches().getFirst().spectators().size());

        // new overload with empty map
        GameHistory historyNew = GameMapper.toDomain(game, List.of(match), playersByMatch, Map.of());
        assertEquals(0, historyNew.matches().getFirst().spectators().size());
    }

    @Test
    @DisplayName("toDomain com match sem players nem spectators deve retornar listas vazias")
    void toDomainWithNoPlayersOrSpectators() {
        UUID gameId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        GameJpaEntity game = new GameJpaEntity();
        game.setId(gameId); game.setRoomName("r"); game.setGameType(GameType.CUSTOM); game.setStatus(GameStatus.WAITING); game.setRoomCode("C");
        MatchJpaEntity match = new MatchJpaEntity(); match.setId(matchId); match.setGameId(gameId); match.setEndedAt(LocalDateTime.now());
        GameHistory history = GameMapper.toDomain(game, List.of(match), Map.of(), Map.of());
        assertTrue(history.matches().getFirst().players().isEmpty());
        assertTrue(history.matches().getFirst().spectators().isEmpty());
    }

    @Test
    @DisplayName("MatchHistory construtor de compatibilidade deve criar spectators vazio")
    void matchHistoryCompatConstructorShouldCreateEmptySpectators() {
        var players = List.of(new PlayerHistory(UUID.randomUUID(), "p", 0, false));
        var mh = new com.letraaletra.api.features.game.domain.history.MatchHistory(players, java.time.Instant.now());
        assertEquals(1, mh.players().size());
        assertNotNull(mh.spectators());
        assertTrue(mh.spectators().isEmpty());
    }

    @Test
    @DisplayName("toEntity deve mapear allowSpectators e privateGame")
    void toEntityShouldMapRoomSettings() {
        var game = com.letraaletra.api.features.game.domain.Game.create("CODE12", "room", new com.letraaletra.api.features.game.domain.room.RoomSettings(true, true), GameType.CUSTOM);
        var entity = GameMapper.toEntity(game);
        assertTrue(entity.isAllowSpectators());
        assertTrue(entity.isPrivateGame());
        var game2 = com.letraaletra.api.features.game.domain.Game.create("CODE13", "room2", new com.letraaletra.api.features.game.domain.room.RoomSettings(false, false), GameType.CUSTOM);
        var entity2 = GameMapper.toEntity(game2);
        assertFalse(entity2.isAllowSpectators());
        assertFalse(entity2.isPrivateGame());
    }
}
