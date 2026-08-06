package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.RemoveDisconnectedParticipantActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.RemoveParticipantResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
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
class RemoveDisconnectedParticipantUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameTimeoutManager gameTimeoutManager;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private GameOverService gameOverService;

    @Mock
    private Actor actor;

    @Mock
    private User mockUser;

    @InjectMocks
    private RemoveDisconnectedParticipantUseCase useCase;

    private UUID gameId;
    private UUID userId;
    private RemoveDisconnectedParticipantInput input;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        userId = UUID.randomUUID();
        input = new RemoveDisconnectedParticipantInput(gameId, userId);
    }

    @Test
    @DisplayName("Deve iniciar timeout do jogo quando o status retornado for WAITING")
    void shouldStartGameTimeoutWhenStatusIsWaiting() {
        // Arrange
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        Void output = useCase.execute(input);

        // Assert
        assertNull(output);
        verify(gameTimeoutManager).start(game);
        verify(userRepository).save(mockUser);
        verify(gameRepository).save(game);
        verify(actorManager, never()).remove(any());
    }

    @Test
    @DisplayName("Deve remover o ator do ActorManager quando o status do jogo for CLOSED")
    void shouldRemoveActorWhenStatusIsClosed() {
        // Arrange
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getId()).thenReturn(gameId);
        when(game.getGameStatus()).thenReturn(GameStatus.CLOSED);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        Void output = useCase.execute(input);

        // Assert
        assertNull(output);
        verify(actorManager).remove(gameId);
        verify(userRepository).save(mockUser);
        verify(gameRepository).save(game);
        verify(gameTimeoutManager, never()).start(any());
    }

    @Test
    @DisplayName("Deve apenas salvar o jogo sem interagir com timeout ou remoção do ator quando em progresso")
    void shouldOnlySaveGameWhenGameIsInProgress() {
        // Arrange
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        Void output = useCase.execute(input);

        // Assert
        assertNull(output);
        verify(userRepository).save(mockUser);
        verify(gameRepository).save(game);
        verify(gameTimeoutManager, never()).start(any());
        verify(actorManager, never()).remove(any());
    }

    @Test
    @DisplayName("Deve invocar GameOverService quando houver evento de encerramento no resultado")
    void shouldHandleGameOverServiceWhenGameOverIsPresent() {
        // Arrange
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);
        GameOver gameOver = mock(GameOver.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.of(gameOver));

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        useCase.execute(input);

        // Assert
        verify(gameOverService).handle(game, gameOver);
        verify(userRepository).save(mockUser);
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário não for localizado")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        // Arrange
        when(actorManager.get(gameId)).thenReturn(actor);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));
        verify(actor, never()).enqueueCommand(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve enviar o comando de remoção contendo a instância do usuário para o Actor")
    void shouldSendRemoveCommandToActor() {
        // Arrange
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        // Act
        useCase.execute(input);

        // Assert
        ArgumentCaptor<RemoveDisconnectedParticipantActorCommand> captor =
                ArgumentCaptor.forClass(RemoveDisconnectedParticipantActorCommand.class);

        verify(actor).enqueueCommand(captor.capture());

        RemoveDisconnectedParticipantActorCommand capturedCommand = captor.getValue();
        assertNotNull(capturedCommand);
    }
}