package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.service.GameOverHandler;
import com.letraaletra.api.features.game.domain.ExpireTurnResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ExpireTurnTimeoutServiceTest {

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private GameOverHandler gameOverHandler;

    @Mock
    private Actor actor;

    @InjectMocks
    private ExpireTurnTimeoutService service;

    private UUID userId1;
    private UUID userId2;
    private UUID gameId;

    @BeforeEach
    void setup() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        gameId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve expirar o turno com sucesso e construir o output correto")
    void shouldExpireTurnSuccessfully() {
        // Arrange
        Game game = mock(Game.class);
        GameState gameState = mock(GameState.class);

        com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult actorOutput =
                mock(com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult.class);

        when(gameActorManager.get(gameId)).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(actorOutput)));

        when(actorOutput.whoPassed()).thenReturn(userId1);
        when(actorOutput.game()).thenReturn(game);
        when(actorOutput.gameOver()).thenReturn(Optional.empty());

        when(game.getGameState()).thenReturn(gameState);
        when(gameState.currentPlayerTurn()).thenReturn(userId2);

        // Act
        ExpireTurnResult output = service.expire(gameId, 1).orElseThrow();

        // Assert
        assertEquals("TURN_EXPIRED", output.event());
        assertEquals(userId1, output.user());
        assertEquals(userId2, output.currentPlayerTurnId());
        assertEquals(game, output.game());
        assertEquals(Optional.empty(), output.gameOver());

        verify(gameActorManager).get(gameId);
        verify(actor).enqueueCommand(any(ExpireTurnActorCommand.class));
        verifyNoInteractions(gameOverHandler);
    }

    @Test
    @DisplayName("Deve delegar ao GameOverHandler quando o fim de jogo for detectado")
    void shouldHandleGameOverWhenFinished() {
        // Arrange
        Game game = mock(Game.class);
        GameState gameState = mock(GameState.class);

        com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult actorOutput =
                mock(com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult.class);
        GameOver gameOver = mock(GameOver.class);

        when(gameActorManager.get(gameId)).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(actorOutput)));

        when(actorOutput.whoPassed()).thenReturn(userId1);
        when(actorOutput.game()).thenReturn(game);
        when(actorOutput.gameOver()).thenReturn(Optional.of(gameOver));

        when(game.getGameState()).thenReturn(gameState);
        when(gameState.currentPlayerTurn()).thenReturn(userId2);

        // Act
        service.expire(gameId, 1);

        // Assert
        verify(gameOverHandler).handle(game, gameOver);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o comando do Actor retornar Optional.empty()")
    void shouldReturnEmptyWhenActorReturnsEmptyResult() {
        // Arrange
        when(gameActorManager.get(gameId)).thenReturn(actor);

        when(actor.enqueueCommand(any(ExpireTurnActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // Act
        Optional<ExpireTurnResult> output = service.expire(gameId, 1);

        // Assert
        assertTrue(output.isEmpty());
        verifyNoInteractions(gameOverHandler);
    }
}