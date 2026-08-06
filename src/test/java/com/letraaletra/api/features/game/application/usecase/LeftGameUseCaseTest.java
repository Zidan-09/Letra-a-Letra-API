package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeftGameUseCaseTest {

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameTimeoutManager gameTimeoutManager;

    @Mock
    private GameOverService gameOverService;

    @Mock
    private Actor actor;

    @InjectMocks
    private LeftGameUseCase useCase;

    private UUID gameId;
    private LeftGameInput input;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        String session = "session-123";
        input = new LeftGameInput(gameId, session);
    }

    @Test
    @DisplayName("Deve iniciar timeout quando o jogo estiver no status WAITING")
    void shouldStartTimeoutWhenGameStatusIsWaiting() {
        // Arrange
        Game game = mock(Game.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        LeftGameOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(game, output.game());
        assertTrue(output.gameOver().isEmpty());

        verify(gameTimeoutManager).start(game);
        verify(gameRepository).save(game);
        verify(gameOverService, never()).handle(any(), any());
        verify(actorManager, never()).remove(any());
    }

    @Test
    @DisplayName("Deve remover o ator do ActorManager e processar game over quando o jogo estiver no status CLOSED")
    void shouldRemoveActorWhenGameStatusIsClosed() {
        // Arrange
        Game game = mock(Game.class);
        GameOver gameOver = mock(GameOver.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(game.getId()).thenReturn(gameId);
        when(game.getGameStatus()).thenReturn(GameStatus.CLOSED);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.of(gameOver));

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        LeftGameOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(game, output.game());
        assertTrue(output.gameOver().isPresent());
        assertEquals(gameOver, output.gameOver().get());

        verify(gameOverService).handle(game, gameOver);
        verify(actorManager).remove(gameId);
        verify(gameRepository).save(game);
        verify(gameTimeoutManager, never()).start(any());
    }

    @Test
    @DisplayName("Deve enviar o comando de saída com os parâmetros corretos para o Actor")
    void shouldSendLeftGameCommandToActor() {
        // Arrange
        Game game = mock(Game.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        useCase.execute(input);

        // Assert
        ArgumentCaptor<LeftGameActorCommand> captor = ArgumentCaptor.forClass(LeftGameActorCommand.class);
        verify(actor).enqueueCommand(captor.capture());

        LeftGameActorCommand capturedCommand = captor.getValue();
        assertNotNull(capturedCommand);
    }
}