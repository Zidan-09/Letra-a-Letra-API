package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.JoinGameActorCommand;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
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
class JoinGameUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> actorManager;

    @InjectMocks
    private JoinGameUseCase useCase;

    private UUID userId;
    private UUID gameId;
    private JoinGameInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        gameId = UUID.randomUUID();

        input = new JoinGameInput(
                gameId,
                "session-abc",
                userId
        );
    }

    @Test
    void shouldJoinGameSuccessfully() {
        User user = mock(User.class);
        Game game = mock(Game.class);
        Actor actor = mock(Actor.class);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(true);

        when(actorManager.get(gameId))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(JoinGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        JoinGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(game, output.game());

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.find(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(actorManager, never()).get(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldSendJoinGameCommandToActor() {
        User user = mock(User.class);
        Game game = mock(Game.class);
        Actor actor = mock(Actor.class);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(true);

        when(actorManager.get(gameId))
                .thenReturn(actor);

        when(actor.enqueueCommand(any()))
                .thenReturn(CompletableFuture.completedFuture(game));

        useCase.execute(input);

        ArgumentCaptor<JoinGameActorCommand> captor =
                ArgumentCaptor.forClass(JoinGameActorCommand.class);

        verify(actor).enqueueCommand(captor.capture());

        JoinGameActorCommand command = captor.getValue();

        assertNotNull(command);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyInGame() {
        User user = mock(User.class);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(false);

        assertThrows(
                UserAlreadyInGameException.class,
                () -> useCase.execute(
                        input
                )
        );

        verify(actorManager, never()).get(any());
    }
}