package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.BanParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanParticipantUseCaseTest {

    @Mock
    private ModerationContextService moderationContextService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> gameActorManager;

    @InjectMocks
    private BanParticipantUseCase useCase;

    private UUID gameId;
    private UUID targetUserId;
    private UUID moderatorId;
    private BanParticipantInput input;

    @Mock
    private ModerationContext mockContext;
    @Mock
    private Game mockGame;
    @Mock
    private Actor mockActor;
    @Mock
    private User mockTargetUser;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        moderatorId = UUID.randomUUID();
        input = new BanParticipantInput(gameId, targetUserId, moderatorId);
    }

    @Test
    @DisplayName("Should successfully ban participant, update user state and return outputs when all context checks pass")
    void shouldBanParticipantSuccessfully() {
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId)).thenReturn(mockContext);
        when(mockContext.game()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(BanParticipantActorCommand.class))).thenReturn(future);

        when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockTargetUser));

        BanParticipantOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockGame, output.game());

        verify(moderationContextService, times(1)).resolve(gameId, targetUserId, moderatorId);
        verify(gameActorManager, times(1)).get(gameId);
        verify(mockActor, times(1)).enqueueCommand(any(BanParticipantActorCommand.class));
        verify(mockTargetUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockTargetUser);
    }

    @Test
    @DisplayName("Should propagate exception and stop processing when moderation security or resolution context fails")
    void shouldThrowExceptionWhenModerationResolutionFails() {
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId))
                .thenThrow(new SecurityException("User has no moderator privileges over this game session"));

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(gameActorManager);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should propagate CompletionException when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandFails() {
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId)).thenReturn(mockContext);
        when(mockContext.game()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor system mailbox overloaded or terminated"));
        when(mockActor.enqueueCommand(any(BanParticipantActorCommand.class))).thenReturn(failedFuture);

        assertThrows(CompletionException.class, () -> useCase.execute(input));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException and avoid domain state persistence when target profile cannot be found")
    void shouldThrowUserNotFoundExceptionWhenTargetDoesNotExist() {
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId)).thenReturn(mockContext);
        when(mockContext.game()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(BanParticipantActorCommand.class))).thenReturn(future);

        when(userRepository.find(targetUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
    }
}