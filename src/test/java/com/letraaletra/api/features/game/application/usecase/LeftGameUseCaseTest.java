package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
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
class LeftGameUseCaseTest {

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameOverFinalizer gameOverFinalizer;

    @Mock
    private Actor actor;

    @InjectMocks
    private LeftGameUseCase useCase;

    private UUID gameId;
    private UUID userId;
    private User user;
    private LeftGameInput input;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        user = UserFactory.createLocal("leaver", "leaver@test.com", "hash");
        userId = user.getUserId();
        String session = "session-123";
        input = new LeftGameInput(gameId, userId, session);
    }

    @Test
    @DisplayName("Deve remover o ator do ActorManager e salvar o usuário quando o status for CLOSED e sem GameOver")
    void shouldRemoveActorAndSaveUserWhenGameStatusIsClosed() {
        Game game = mock(Game.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(game.getId()).thenReturn(gameId);
        when(game.getGameStatus()).thenReturn(GameStatus.CLOSED);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        LeftGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(game, output.game());
        assertTrue(output.gameOver().isEmpty());

        verify(actorManager).remove(gameId);
        verify(userRepository).save(user);
        verify(gameOverFinalizer, never()).finish(any(), any());
    }

    @Test
    @DisplayName("Deve processar GameOverFinalizer e retornar antecipadamente quando houver GameOver")
    void shouldProcessGameOverFinalizerWhenGameOverIsPresent() {
        Game game = mock(Game.class);
        GameOver gameOver = mock(GameOver.class);
        HandledGameOver handledGameOver = mock(HandledGameOver.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.of(gameOver));
        when(gameOverFinalizer.finish(game, gameOver)).thenReturn(handledGameOver);

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        LeftGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(game, output.game());
        assertTrue(output.gameOver().isPresent());
        assertEquals(handledGameOver, output.handledGameOver());

        verify(gameOverFinalizer).finish(game, gameOver);
        verify(actorManager, never()).remove(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve apenas salvar o usuário sem remover ator quando a partida continuar em andamento (RUNNING)")
    void shouldOnlySaveUserWhenGameIsStillRunning() {
        Game game = mock(Game.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        LeftGameOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(userRepository).save(user);
        verify(actorManager, never()).remove(any());
        verify(gameOverFinalizer, never()).finish(any(), any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException se o usuário não for encontrado")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));
        verify(actorManager, never()).get(any());
    }

    @Test
    @DisplayName("Deve enviar o comando LeftGameActorCommand correto ao Ator")
    void shouldSendLeftGameCommandToActor() {
        Game game = mock(Game.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        useCase.execute(input);

        ArgumentCaptor<LeftGameActorCommand> captor = ArgumentCaptor.forClass(LeftGameActorCommand.class);
        verify(actor).enqueueCommand(captor.capture());

        LeftGameActorCommand capturedCommand = captor.getValue();
        assertNotNull(capturedCommand);
    }
}