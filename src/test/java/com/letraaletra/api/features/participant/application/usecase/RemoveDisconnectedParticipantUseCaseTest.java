package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.RemoveDisconnectedParticipantActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.RemoveParticipantResult;
import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
    private ActorManager<Game> actorManager;

    @Mock
    private GameOverFinalizer gameOverFinalizer;

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
    @DisplayName("Deve remover o ator do ActorManager e salvar usuário quando o status do jogo for CLOSED")
    void shouldRemoveActorAndSaveUserWhenStatusIsClosed() {
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

        Void output = useCase.execute(input);

        assertNull(output);
        verify(actorManager).remove(gameId);
        verify(userRepository).save(mockUser);
        verify(gameOverFinalizer, never()).finish(any(), any());
    }

    @Test
    @DisplayName("Deve apenas salvar o usuário sem remover o ator quando a partida continuar em andamento (RUNNING)")
    void shouldOnlySaveUserWhenGameIsStillRunning() {
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        Void output = useCase.execute(input);

        assertNull(output);
        verify(userRepository).save(mockUser);
        verify(actorManager, never()).remove(any());
        verify(gameOverFinalizer, never()).finish(any(), any());
    }

    @Test
    @DisplayName("Deve invocar GameOverFinalizer e encerrar sem salvar o usuário quando houver GameOver")
    void shouldHandleGameOverFinalizerWhenGameOverIsPresent() {
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);
        GameOver gameOver = mock(GameOver.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.of(gameOver));

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        useCase.execute(input);

        verify(gameOverFinalizer).finish(game, gameOver);
        verify(userRepository, never()).save(any());
        verify(actorManager, never()).remove(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário não for localizado")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        when(actorManager.get(gameId)).thenReturn(actor);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));
        verify(actor, never()).enqueueCommand(any());
    }

    @Test
    @DisplayName("Deve enviar o comando de remoção contendo a instância do usuário para o Actor")
    void shouldSendRemoveCommandToActor() {
        Game game = mock(Game.class);
        RemoveParticipantResult result = mock(RemoveParticipantResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(RemoveDisconnectedParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        useCase.execute(input);

        ArgumentCaptor<RemoveDisconnectedParticipantActorCommand> captor =
                ArgumentCaptor.forClass(RemoveDisconnectedParticipantActorCommand.class);

        verify(actor).enqueueCommand(captor.capture());

        RemoveDisconnectedParticipantActorCommand capturedCommand = captor.getValue();
        assertNotNull(capturedCommand);
    }
}