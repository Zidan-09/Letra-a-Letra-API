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

        lenient().when(inv.getItems()).thenReturn(Collections.emptyList());

        User user = mock(User.class);
        lenient().when(user.getUserId()).thenReturn(userId);
        lenient().when(user.getUsername()).thenReturn("u-" + userId.toString().substring(0, 4));
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
        Board board = BoardGenerator.generate(List.of("alpha", "bravo", "charlie", "delta", "echo"), GameMode.NORMAL);
        game.start(board);
        return game;
    }

    @Test
    @DisplayName("toGlobalResponse should throw when gameState is null")
    void shouldThrowWhenGameStateIsNull() {
        Game game = createWaitingGame();
        assertNull(game.getGameState());

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
    @DisplayName("toGlobalResponse should map correctly when game is running and state is present")
    void shouldMapGlobalWhenRunning() {
        Game game = createRunningGame();

        GameStateResponse resp = GameStateResponseMapper.toGlobalResponse(game);

        assertNotNull(resp);
        assertEquals(2, resp.players().size());
        assertNotNull(resp.board());
        assertNotNull(resp.words());
        assertFalse(resp.words().isEmpty());
        assertNotNull(resp.currentTurnPlayerId());
        assertTrue(resp.players().stream().allMatch(p -> p != null && p.id() != null));
    }

    @Test
    @DisplayName("toResponse per viewer should map correctly when game is running and state is present")
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
    @DisplayName("should throw GameNotRunningException explicitly when game state is null")
    void shouldThrowExplicitWhenRunningNull() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE", "room", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User u1 = mockUser(UUID.randomUUID());
        User u2 = mockUser(UUID.randomUUID());
        game.join(u1, "s1");
        game.join(u2, "s2");
        game.setGameStatus(GameStatus.RUNNING);

        assertNull(game.getGameState());

        assertThrows(GameNotRunningException.class, () -> GameStateResponseMapper.toGlobalResponse(game));
    }
}