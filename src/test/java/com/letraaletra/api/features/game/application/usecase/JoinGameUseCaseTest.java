package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.JoinGameActorCommand;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.domain.security.TokenService;
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
    private TokenService tokenService;

    @Mock
    private ActorManager<Game> actorManager;

    @InjectMocks
    private JoinGameUseCase useCase;

    private UUID userId;
    private UUID gameId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        gameId = UUID.randomUUID();
    }

    @Test
    void shouldJoinGameSuccessfully() {
        JoinGameInput input = new JoinGameInput(
                "token-123",
                "session-123",
                userId
        );

        User user = mock(User.class);
        Game game = mock(Game.class);
        Actor actor = mock(Actor.class);

        when(tokenService.getTokenContent("token-123"))
                .thenReturn(gameId);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(true);

        when(actorManager.get(gameId.toString()))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(JoinGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        JoinGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals("token-123", output.token());
        assertEquals(game, output.game());

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        JoinGameInput input = new JoinGameInput(
                "token-123",
                "session-123",
                userId
        );

        when(tokenService.getTokenContent("token-123"))
                .thenReturn(gameId);

        when(userRepository.find(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(actorManager, never()).get(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldSendJoinGameCommandToActor() {
        JoinGameInput input = new JoinGameInput(
                "token-123",
                "session-abc",
                userId
        );

        User user = mock(User.class);
        Game game = mock(Game.class);
        Actor actor = mock(Actor.class);

        when(tokenService.getTokenContent("token-123"))
                .thenReturn(gameId);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(true);

        when(actorManager.get(gameId.toString()))
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

        when(tokenService.getTokenContent("token"))
                .thenReturn(gameId);

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(false);

        assertThrows(
                UserAlreadyInGameException.class,
                () -> useCase.execute(
                        new JoinGameInput(
                                "token",
                                "session",
                                userId
                        )
                )
        );

        verify(actorManager, never()).get(anyString());
    }
}