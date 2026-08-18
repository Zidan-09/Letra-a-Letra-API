package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.DisconnectParticipantActorCommand;
import com.letraaletra.api.features.game.domain.service.DisconnectScheduler;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
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
class DisconnectUseCaseTest {

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private DisconnectScheduler disconnectScheduler;

    @Mock
    private MatchmakingRepository matchmakingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DisconnectUseCase useCase;

    private UUID userId;
    private UUID gameId;
    private DisconnectParticipantInput input;

    @Mock
    private User mockUser;

    @Mock
    private Actor mockActor;

    @Mock
    private Game mockGame;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        gameId = UUID.randomUUID();
        input = new DisconnectParticipantInput(userId, "session");
    }

    @Test
    @DisplayName("Should return empty Optional immediately when the input user identifier context is null")
    void shouldReturnEmptyWhenUserIdIsNull() {
        // Arrange
        DisconnectParticipantInput nullInput = new DisconnectParticipantInput(null, null);

        // Act
        Optional<DisconnectParticipantOutput> result = useCase.execute(nullInput);

        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(matchmakingRepository, userRepository, gameActorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should remove user from matchmaking queue if they are currently waiting in line")
    void shouldRemoveUserFromQueueWhenUserIsOnMatchmaking() {
        // Arrange
        when(matchmakingRepository.onQueue(userId)).thenReturn(true);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);

        // Act
        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        // Assert
        assertTrue(result.isEmpty());
        verify(matchmakingRepository).onQueue(userId);
        verify(matchmakingRepository).remove(userId);
        verify(userRepository).find(userId);
        verifyNoInteractions(gameActorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when target user does not exist in repository")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository).find(userId);
        verifyNoInteractions(gameActorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should return empty Optional when user context exists but user profile state is not in a live game")
    void shouldReturnEmptyWhenUserIsNotInAGame() {
        // Arrange
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);

        // Act
        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        // Assert
        assertTrue(result.isEmpty());
        verify(matchmakingRepository, never()).remove(any());
        verifyNoInteractions(gameActorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should successfully handle active game participant disconnect, scheduling disconnect timer")
    void shouldDisconnectActiveParticipantSuccessfully() {
        // Arrange
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        when(mockGame.getId()).thenReturn(gameId);
        CompletableFuture<Optional<Game>> future = CompletableFuture.completedFuture(Optional.of(mockGame));
        when(mockActor.enqueueCommand(any(DisconnectParticipantActorCommand.class))).thenReturn(future);

        // Act
        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().user());
        assertEquals(mockGame, result.get().game());

        verify(disconnectScheduler).start(userId, gameId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should force user state cleanup and save changes locally if actor mailbox signals game state is dead")
    void shouldCleanUpUserStateWhenActorReturnsEmptyGameState() {
        // Arrange
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> future = CompletableFuture.completedFuture(Optional.empty());
        when(mockActor.enqueueCommand(any(DisconnectParticipantActorCommand.class))).thenReturn(future);

        // Act
        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockUser).leaveGame();
        verify(userRepository).save(mockUser);
        verifyNoInteractions(disconnectScheduler);
    }

    @Test
    @DisplayName("Should propagate CompletionException directly when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        // Arrange
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor context thread pool heavily degraded"));
        when(mockActor.enqueueCommand(any(DisconnectParticipantActorCommand.class))).thenReturn(failedFuture);

        // Act & Assert
        assertThrows(CompletionException.class, () -> useCase.execute(input));
        verify(userRepository, never()).save(any());
        verifyNoInteractions(disconnectScheduler);
    }
}