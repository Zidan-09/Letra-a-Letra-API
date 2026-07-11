package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.DisconnectScheduler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.DisconnectParticipantActorCommand;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.user.domain.User;
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
        DisconnectParticipantInput nullInput = new DisconnectParticipantInput(null, null);

        Optional<DisconnectParticipantOutput> result = useCase.execute(nullInput);

        assertTrue(result.isEmpty());
        verifyNoInteractions(matchmakingRepository, userRepository, gameActorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should remove user from matchmaking queue if they are currently waiting in line")
    void shouldRemoveUserFromQueueWhenUserIsOnMatchmaking() {
        when(matchmakingRepository.onQueue(userId)).thenReturn(true);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verify(matchmakingRepository, times(1)).onQueue(userId);
        verify(matchmakingRepository, times(1)).remove(userId);
    }

    @Test
    @DisplayName("Should return empty Optional when user context exists but user profile state is not in a live game")
    void shouldReturnEmptyWhenUserIsNotInAGame() {
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);

        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verify(matchmakingRepository, never()).remove(any());
        verifyNoInteractions(gameActorManager);
    }

    @Test
    @DisplayName("Should successfully handle active game participant disconnect, leaving actor pipeline alive")
    void shouldDisconnectActiveParticipantSuccessfully() {
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> future = CompletableFuture.completedFuture(Optional.of(mockGame));
        when(mockActor.enqueueCommand(any(DisconnectParticipantActorCommand.class))).thenReturn(future);

        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().user());
        assertEquals(mockGame, result.get().game());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should force user state cleanup and save changes locally if actor mailbox signals game state is dead")
    void shouldCleanUpUserStateWhenActorReturnsEmptyGameState() {
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> future = CompletableFuture.completedFuture(Optional.empty());
        when(mockActor.enqueueCommand(any(DisconnectParticipantActorCommand.class))).thenReturn(future);

        Optional<DisconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verify(mockUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("Should propagate CompletionException directly when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor context thread pool heavily degraded"));
        when(mockActor.enqueueCommand(any(DisconnectParticipantActorCommand.class))).thenReturn(failedFuture);

        assertThrows(CompletionException.class, () -> useCase.execute(input));
        verify(userRepository, never()).save(any());
    }
}