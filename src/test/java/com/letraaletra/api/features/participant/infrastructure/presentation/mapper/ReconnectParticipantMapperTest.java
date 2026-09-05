package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.generator.BoardGenerator;
import com.letraaletra.api.features.game.domain.exception.GameNotRunningException;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.state.GameStateFactory;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReconnectParticipantMapperTest {

    private User mockUser(UUID userId, Inventory inventory) {
        User user = mock(User.class);
        lenient().when(user.getUserId()).thenReturn(userId);
        lenient().when(user.getUsername()).thenReturn("user-" + userId.toString().substring(0, 4));
        lenient().when(user.getInventory()).thenReturn(inventory);
        return user;
    }

    @Test
    @DisplayName("WAITING + gameState null -> room != null, gameState == null")
    void shouldMapToLobbyWhenWaitingWithNullState() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE1", "Lobby", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);

        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User u1 = mockUser(UUID.randomUUID(), inv);
        User u2 = mockUser(UUID.randomUUID(), inv);

        game.join(u1, "s1");
        game.join(u2, "s2");

        assertEquals(GameStatus.WAITING, game.getGameStatus());
        assertNull(game.getGameState());

        ReconnectParticipantOutput output = new ReconnectParticipantOutput(game);

        ReconnectParticipantResponse response = ReconnectParticipantMapper.toResponse(output);

        assertNotNull(response.room());
        assertEquals(GameStatus.WAITING, response.room().status());
        assertEquals(game.getId().toString(), response.room().gameId());
        assertEquals(2, response.room().participants().size());
        assertNull(response.gameState());
    }

    @Test
    @DisplayName("WAITING + stale gameState -> still lobby, gameState == null")
    void shouldMapToLobbyWhenWaitingWithStaleState() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE2", "Lobby2", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);

        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User u1 = mockUser(UUID.randomUUID(), inv);
        User u2 = mockUser(UUID.randomUUID(), inv);

        game.join(u1, "s1");
        game.join(u2, "s2");

        // force RUNNING then back to WAITING with stale state
        Board board = BoardGenerator.generate(List.of("alpha", "bravo", "charlie", "delta", "echo"), com.letraaletra.api.features.game.domain.state.GameMode.NORMAL);
        game.start(board);
        assertEquals(GameStatus.RUNNING, game.getGameStatus());
        GameState stale = game.getGameState();
        assertNotNull(stale);

        game.setGameStatus(GameStatus.WAITING);
        // stale remains

        ReconnectParticipantOutput output = new ReconnectParticipantOutput(game);
        ReconnectParticipantResponse response = ReconnectParticipantMapper.toResponse(output);

        assertNotNull(response.room());
        assertEquals(GameStatus.WAITING, response.room().status());
        assertNull(response.gameState(), "WAITING with stale state must not expose gameState");
    }

    @Test
    @DisplayName("RUNNING + valid gameState -> room != null and gameState != null")
    void shouldMapToGameStateWhenRunning() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE3", "Lobby3", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);

        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User u1 = mockUser(UUID.randomUUID(), inv);
        User u2 = mockUser(UUID.randomUUID(), inv);

        game.join(u1, "s1");
        game.join(u2, "s2");

        Board board = BoardGenerator.generate(List.of("alpha", "bravo", "charlie", "delta", "echo"), com.letraaletra.api.features.game.domain.state.GameMode.NORMAL);
        game.start(board);

        assertEquals(GameStatus.RUNNING, game.getGameStatus());
        assertNotNull(game.getGameState());

        ReconnectParticipantOutput output = new ReconnectParticipantOutput(game);
        ReconnectParticipantResponse response = ReconnectParticipantMapper.toResponse(output);

        assertNotNull(response.room());
        assertEquals(GameStatus.RUNNING, response.room().status());
        assertNotNull(response.gameState());
        assertEquals(2, response.gameState().players().size());
        assertNotNull(response.gameState().board());
        assertNotNull(response.gameState().currentTurnPlayerId());
        assertNotNull(response.gameState().words());
    }

    @Test
    @DisplayName("RUNNING + null gameState -> throw GameNotRunningException")
    void shouldThrowWhenRunningWithNullState() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE4", "Lobby4", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);

        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User u1 = mockUser(UUID.randomUUID(), inv);
        User u2 = mockUser(UUID.randomUUID(), inv);

        game.join(u1, "s1");
        game.join(u2, "s2");

        // manually set RUNNING without state to simulate invariant violation
        game.setGameStatus(GameStatus.RUNNING);
        assertNull(game.getGameState());

        ReconnectParticipantOutput output = new ReconnectParticipantOutput(game);

        assertThrows(GameNotRunningException.class, () -> ReconnectParticipantMapper.toResponse(output));
    }
}
