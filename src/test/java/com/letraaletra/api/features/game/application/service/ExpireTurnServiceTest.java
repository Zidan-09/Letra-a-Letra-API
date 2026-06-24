package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.application.input.ExpireTurnInput;
import com.letraaletra.api.features.game.application.output.ExpireTurnOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpireTurnServiceTest {

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private GameOverHandler gameOverHandler;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Actor actor;

    @InjectMocks
    private ExpireTurnService service;

    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setup() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
    }

    @Test
    void shouldExpireTurnSuccessfully() {
        ExpireTurnInput input = new ExpireTurnInput("game-1", 1);

        Game game = mock(Game.class);
        var gameState = mock(com.letraaletra.api.features.game.domain.state.GameState.class);

        ExpireTurnResult result = mock(ExpireTurnResult.class);
        GameOverResult gameOverResult = mock(GameOverResult.class);

        when(gameActorManager.get("game-1")).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(result)));

        when(result.whoPassed()).thenReturn(userId1);
        when(result.game()).thenReturn(game);
        when(result.gameOverResult()).thenReturn(gameOverResult);
        when(result.removedBecauseAfk()).thenReturn(false);

        when(game.getGameState()).thenReturn(gameState);
        when(gameState.currentPlayerTurn()).thenReturn(userId2);

        ExpireTurnOutput output = service.execute(input).orElseThrow();

        assertEquals("TURN_EXPIRED", output.event());
        assertEquals(userId1, output.user());
        assertEquals(userId2, output.currentPlayerTurnId());
        assertEquals(game, output.game());
        assertEquals(gameOverResult, output.gameOverResult());
        assertFalse(output.removedBecauseAfk());

        verify(gameActorManager).get("game-1");
        verify(actor).enqueueCommand(any(ExpireTurnActorCommand.class));
    }

    @Test
    void shouldHandleAfkRemovalWhenUserIsRemoved() {
        ExpireTurnInput input = new ExpireTurnInput("game-1", 1);

        Game game = mock(Game.class);
        var gameState = mock(com.letraaletra.api.features.game.domain.state.GameState.class);

        User user = mock(User.class);

        ExpireTurnResult result = mock(ExpireTurnResult.class);
        GameOverResult gameOverResult = mock(GameOverResult.class);

        when(gameActorManager.get("game-1")).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(result)));

        when(result.whoPassed()).thenReturn(userId1);
        when(result.game()).thenReturn(game);
        when(result.gameOverResult()).thenReturn(gameOverResult);
        when(result.removedBecauseAfk()).thenReturn(true);

        when(game.getGameState()).thenReturn(gameState);
        when(gameState.currentPlayerTurn()).thenReturn(userId2);

        when(userRepository.find(userId1)).thenReturn(Optional.of(user));

        service.execute(input);

        verify(user).leaveGame();
        verify(userRepository).save(user);
    }

    @Test
    void shouldHandleGameOverWhenFinished() {
        ExpireTurnInput input = new ExpireTurnInput("game-1", 1);

        Game game = mock(Game.class);
        var gameState = mock(com.letraaletra.api.features.game.domain.state.GameState.class);

        ExpireTurnResult result = mock(ExpireTurnResult.class);
        GameOverResult gameOverResult = mock(GameOverResult.class);

        when(gameActorManager.get("game-1")).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(result)));

        when(result.whoPassed()).thenReturn(userId1);
        when(result.game()).thenReturn(game);
        when(result.gameOverResult()).thenReturn(gameOverResult);
        when(result.removedBecauseAfk()).thenReturn(false);

        when(game.getGameState()).thenReturn(gameState);
        when(gameState.currentPlayerTurn()).thenReturn(userId2);

        when(gameOverResult.finished()).thenReturn(true);

        service.execute(input);

        verify(gameOverHandler).handle(game, gameOverResult);
    }

    @Test
    void shouldReturnEmptyWhenActorReturnsEmptyResult() {
        ExpireTurnInput input = new ExpireTurnInput("game-1", 1);

        when(gameActorManager.get("game-1")).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        Optional<ExpireTurnOutput> output = service.execute(input);

        assertTrue(output.isEmpty());
        verifyNoInteractions(gameOverHandler);
        verifyNoInteractions(userRepository);
    }
}