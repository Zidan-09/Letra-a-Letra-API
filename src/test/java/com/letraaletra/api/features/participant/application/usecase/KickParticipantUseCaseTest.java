package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.KickParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
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
class KickParticipantUseCaseTest {

    @Mock
    private ModerationContextService moderationContextService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> gameActorManager;

    @InjectMocks
    private KickParticipantUseCase useCase;

    private UUID gameId;
    private UUID targetUserId;
    private UUID moderatorId;
    private KickParticipantInput input;

    @Mock
    private ModerationContext mockContext;

    @Mock
    private Game mockGame;

    @Mock
    private Actor mockActor;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        moderatorId = UUID.randomUUID();
        input = new KickParticipantInput(gameId, targetUserId, moderatorId);
    }

    @Test
    @DisplayName("Should successfully kick participant, process async actor command, and update user status")
    void shouldKickParticipantSuccessfully() {
        // Arrange
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId)).thenReturn(mockContext);
        when(mockContext.game()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(KickParticipantActorCommand.class))).thenReturn(future);

        when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));

        // Act
        KickParticipantOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(mockGame, output.game());

        verify(moderationContextService, times(1)).resolve(gameId, targetUserId, moderatorId);
        verify(gameActorManager, times(1)).get(gameId);
        verify(mockActor, times(1)).enqueueCommand(any(KickParticipantActorCommand.class));
        verify(userRepository, times(1)).find(targetUserId);
        verify(mockUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when target user does not exist in repository")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId)).thenReturn(mockContext);
        when(mockContext.game()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(KickParticipantActorCommand.class))).thenReturn(future);

        when(userRepository.find(targetUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository, times(1)).find(targetUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception and halt workflow when moderation security or validation context resolution fails")
    void shouldThrowExceptionWhenModerationContextFailsToResolve() {
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId))
                .thenThrow(new SecurityException("User does not have required permissions to kick from this session"));

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(gameActorManager);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should propagate CompletionException directly when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        when(moderationContextService.resolve(gameId, targetUserId, moderatorId)).thenReturn(mockContext);
        when(mockContext.game()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor system mailbox processing interrupted"));
        when(mockActor.enqueueCommand(any(KickParticipantActorCommand.class))).thenReturn(failedFuture);

        assertThrows(CompletionException.class, () -> useCase.execute(input));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw NullPointerException when the input parameter is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verifyNoInteractions(moderationContextService);
        verifyNoInteractions(gameActorManager);
        verifyNoInteractions(userRepository);
    }
}