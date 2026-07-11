package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.UnbanParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
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
class UnbanUserUseCaseTest {

    @Mock
    private ActorManager<Game> gameActorManager;

    @InjectMocks
    private UnbanUserUseCase useCase;

    private UUID gameId;
    private UnbanParticipantInput input;

    @Mock
    private Actor mockActor;

    @Mock
    private Game mockGame;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UUID moderatorId = UUID.randomUUID();
        input = new UnbanParticipantInput(gameId, targetUserId, moderatorId);
    }

    @Test
    @DisplayName("Should successfully unban participant, execute actor command asynchronously, and return wrapped game output")
    void shouldUnbanParticipantSuccessfully() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(UnbanParticipantActorCommand.class))).thenReturn(future);

        UnbanParticipantOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockGame, output.game());

        verify(gameActorManager, times(1)).get(gameId);
        verify(mockActor, times(1)).enqueueCommand(any(UnbanParticipantActorCommand.class));
    }

    @Test
    @DisplayName("Should propagate CompletionException directly when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor context mailbox or processing cluster is dead"));
        when(mockActor.enqueueCommand(any(UnbanParticipantActorCommand.class))).thenReturn(failedFuture);

        assertThrows(CompletionException.class, () -> useCase.execute(input));
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root use case input parameter context itself is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(gameActorManager);
    }
}