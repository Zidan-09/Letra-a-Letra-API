package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.SwapPositionActorCommand;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwapRoomPositionUseCaseTest {

    @Mock
    private ActorManager<Game> gameActorManager;

    @InjectMocks
    private SwapRoomPositionUseCase useCase;

    private UUID gameId;
    private SwapPositionInput input;

    @Mock
    private Actor mockActor;

    @Mock
    private Game mockGame;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        int targetPosition = 3;
        input = new SwapPositionInput(gameId, targetPosition, userId);
    }

    @Test
    @DisplayName("Should successfully swap participant position, execute async actor command, and return wrapped game output")
    void shouldSwapPositionSuccessfully() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(SwapPositionActorCommand.class))).thenReturn(future);

        SwapPositionOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockGame, output.game());

        verify(gameActorManager, times(1)).get(gameId);
        verify(mockActor, times(1)).enqueueCommand(any(SwapPositionActorCommand.class));
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when the actor manager returns a null reference actor instance")
    void shouldThrowGameNotFoundExceptionWhenActorIsNull() {
        when(gameActorManager.get(gameId)).thenReturn(null);

        assertThrows(GameNotFoundException.class, () -> useCase.execute(input));

        verify(gameActorManager, times(1)).get(gameId);
    }

    @Test
    @DisplayName("Should propagate CompletionException directly when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor thread pool or dynamic room router is dead"));
        when(mockActor.enqueueCommand(any(SwapPositionActorCommand.class))).thenReturn(failedFuture);

        assertThrows(CompletionException.class, () -> useCase.execute(input));
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root use case input parameter context itself is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(gameActorManager);
    }
}