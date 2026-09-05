package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.generator.BoardGenerator;
import com.letraaletra.api.features.game.domain.exception.GameNotRunningException;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameStateResponseMapperTest {

    private User mockUser(UUID userId) {
        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User user = mock(User.class);
        lenient().when(user.getUserId()).thenReturn(userId);
        lenient().when(user.getUsername()).thenReturn("u-" + userId.toString().substring(0,4));
        lenient().when(user.getInventory()).thenReturn(inv);
        return user;
    }

    private Game createWaitingGame() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE", "room", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        game.join(mockUser(UUID.randomUUID()), "s1");
        game.join(mockUser(UUID.randomUUID()), "s2");
        return game;
    }

    private Game createRunningGame() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE", "room", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User u1 = mockUser(UUID.randomUUID());
        User u2 = mockUser(UUID.randomUUID());
        game.join(u1, "s1");
        game.join(u2, "s2");
        Board board = BoardGenerator.generate(List.of("alpha","bravo","charlie","delta","echo"), GameMode.NORMAL);
        game.start(board);
        return game;
    }

    @Test
    @DisplayName("toGlobalResponse should throw when gameState is null")
    void shouldThrowWhenGameStateIsNull() {
        Game game = createWaitingGame();
        assertNull(game.getGameState());
        assertEquals(GameStatus.WAITING, game.getGameStatus());

        assertThrows(GameNotRunningException.class, () -> GameStateResponseMapper.toGlobalResponse(game));
    }

    @Test
    @DisplayName("toResponse should throw when gameState is null")
    void shouldThrowToResponseWhenNull() {
        Game game = createWaitingGame();
        UUID viewer = game.getParticipants().getParticipants().getFirst().getUserId();
        assertThrows(GameNotRunningException.class, () -> GameStateResponseMapper.toResponse(game, viewer));
    }

    @Test
    @DisplayName("should throw when status is WAITING even if state present (stale)")
    void shouldThrowWhenStatusWaitingEvenIfStatePresent() {
        Game game = createRunningGame();
        GameStatus staleStatus = GameStatus.WAITING;
        game.setGameStatus(staleStatus);
        assertNotNull(game.getGameState());

        assertThrows(GameNotRunningException.class, () -> GameStateResponseMapper.toGlobalResponse(game));
        UUID viewer = game.getParticipants().getParticipants().getFirst().getUserId();
        assertThrows(GameNotRunningException.class, () -> GameStateResponseMapper.toResponse(game, viewer));
    }

    @Test
    @DisplayName("toGlobalResponse should map correctly when RUNNING")
    void shouldMapGlobalWhenRunning() {
        Game game = createRunningGame();

        GameStateResponse resp = GameStateResponseMapper.toGlobalResponse(game);

        assertNotNull(resp);
        assertEquals(2, resp.players().size());
        assertNotNull(resp.board());
        assertEquals(10, resp.board().length);
        assertNotNull(resp.words());
        assertFalse(resp.words().isEmpty());
        assertNotNull(resp.currentTurnPlayerId());
        // check that playerIds match participants
        assertTrue(resp.players().stream().allMatch(p -> p != null && p.id() != null));
    }

    @Test
    @DisplayName("toResponse per viewer should map correctly when RUNNING")
    void shouldMapPerViewerWhenRunning() {
        Game game = createRunningGame();
        UUID viewer = game.getParticipants().getParticipants().getFirst().getUserId();

        GameStateResponse resp = GameStateResponseMapper.toResponse(game, viewer);

        assertNotNull(resp);
        assertEquals(2, resp.players().size());
        assertNotNull(resp.board());
        assertNotNull(resp.currentTurnPlayerId());
    }

    @Test
    @DisplayName("RUNNING + null state should throw not NPE")
    void shouldThrowExplicitWhenRunningNull() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE", "room", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User u1 = mockUser(UUID.randomUUID());
        User u2 = mockUser(UUID.randomUUID());
        game.join(u1, "s1");
        game.join(u2, "s2");
        game.setGameStatus(GameStatus.RUNNING);
        assertNull(game.getGameState());

        GameNotRunningException ex = assertThrows(GameNotRunningException.class, () -> GameStateResponseMapper.toGlobalResponse(game));
        assertNotNull(ex.getMessage());
        assertDoesNotThrow(() -> {
            try { GameStateResponseMapper.toGlobalResponse(game); } catch (GameNotRunningException e) { /*expected*/ }
        });
        // ensure it's not NullPointerException
        try {
            GameStateResponseMapper.toGlobalResponse(game);
            fail("should throw");
        } catch (Exception e) {
            assertFalse(e instanceof NullPointerException, "should not be NPE");
            assertTrue(e instanceof GameNotRunningException);
        }
    }
}
