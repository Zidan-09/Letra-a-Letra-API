package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.domain.actor.output.LeftGameResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
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
    private Actor actor;

    @InjectMocks
    private LeftGameUseCase useCase;

    private UUID gameId;
    private UUID userId;
    private LeftGameInput input;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        userId = UUID.randomUUID();
        input = new LeftGameInput(
                gameId,
                "session-123"
        );
    }

    @Test
    void shouldLeaveGameSuccessfully() {
        Game game = mock(Game.class);
        User user = mock(User.class);
        GameOverResult gameOverResult = mock(GameOverResult.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        when(result.user())
                .thenReturn(userId);

        when(result.game())
                .thenReturn(game);

        when(result.gameOverResult())
                .thenReturn(gameOverResult);

        when(result.isEmpty())
                .thenReturn(false);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        LeftGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(game, output.game());
        assertEquals(gameOverResult, output.gameOverResult());

        verify(user).leaveGame();
        verify(userRepository).save(user);

        verify(actorManager, never()).remove(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldCloseGameWhenLastPlayerLeaves() {
        Game game = mock(Game.class);
        User user = mock(User.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        when(result.user())
                .thenReturn(userId);

        when(result.game())
                .thenReturn(game);

        when(result.isEmpty())
                .thenReturn(true);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(game.getId())
                .thenReturn(gameId);

        useCase.execute(input);

        verify(user).leaveGame();
        verify(userRepository).save(user);

        verify(actorManager).remove(gameId);
        verify(game).setGameStatus(GameStatus.CLOSED);
        verify(gameRepository).save(game);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        when(result.user())
                .thenReturn(userId);

        when(userRepository.find(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldSendLeftGameCommandToActor() {
        Game game = mock(Game.class);
        User user = mock(User.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId))
                .thenReturn(actor);

        when(actor.enqueueCommand(any()))
                .thenReturn(CompletableFuture.completedFuture(result));

        when(result.user())
                .thenReturn(userId);

        when(result.game())
                .thenReturn(game);

        when(result.isEmpty())
                .thenReturn(false);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        useCase.execute(input);

        ArgumentCaptor<LeftGameActorCommand> captor =
                ArgumentCaptor.forClass(LeftGameActorCommand.class);

        verify(actor).enqueueCommand(captor.capture());

        assertNotNull(captor.getValue());
    }
}