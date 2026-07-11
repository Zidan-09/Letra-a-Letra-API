package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.DisconnectScheduler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.domain.Participant;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconnectUseCaseTest {

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private DisconnectScheduler disconnectScheduler;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReconnectUseCase useCase;

    private UUID userId;
    private UUID gameId;
    private String sessionToken;
    private ReconnectParticipantInput input;

    @Mock
    private User mockUser;

    @Mock
    private Actor mockActor;

    @Mock
    private Game mockGame;

    @Mock
    private Participant mockParticipant;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        gameId = UUID.randomUUID();
        sessionToken = "websocket-session-xyz";
        input = new ReconnectParticipantInput(userId, sessionToken);
    }

    @Test
    @DisplayName("Should return empty Optional immediately when the input user identifier context is null")
    void shouldReturnEmptyWhenUserIdIsNull() {
        ReconnectParticipantInput nullInput = new ReconnectParticipantInput(null, sessionToken);

        Optional<ReconnectParticipantOutput> result = useCase.execute(nullInput);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository, actorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should return empty Optional when user context profile cannot be found in repository")
    void shouldReturnEmptyWhenUserNotFound() {
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        Optional<ReconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verifyNoInteractions(actorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should return empty Optional when user is fetched but is currently not mapped inside a live game session")
    void shouldReturnEmptyWhenUserIsNotInAGame() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);

        Optional<ReconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verifyNoInteractions(actorManager, disconnectScheduler);
    }

    @Test
    @DisplayName("Should successfully reconnect participant, cancel pending disconnect task and return updated game structure")
    void shouldReconnectParticipantSuccessfully() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(actorManager.get(gameId)).thenReturn(mockActor);
        when(mockActor.getGame()).thenReturn(mockGame);
        when(mockGame.getParticipantByUserId(userId)).thenReturn(mockParticipant);
        when(mockGame.getId()).thenReturn(gameId);

        Optional<ReconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isPresent());
        assertEquals(mockGame, result.get().game()); // Assumindo record component ou getter .game()

        verify(disconnectScheduler, times(1)).cancel(userId, gameId);
        verify(mockGame, times(1)).reconnect(userId, sessionToken);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty Optional when actor match registers user but game context reports player is missing")
    void shouldReturnEmptyWhenParticipantIsMissingInGame() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(actorManager.get(gameId)).thenReturn(mockActor);
        when(mockActor.getGame()).thenReturn(mockGame);
        when(mockGame.getParticipantByUserId(userId)).thenReturn(null);

        Optional<ReconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verifyNoInteractions(disconnectScheduler);
    }

    @Test
    @DisplayName("Should clear user game states and persist changes locally when internal engine or actor communications crash")
    void shouldCleanUpUserStateWhenExceptionOccurs() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);
        when(actorManager.get(gameId)).thenThrow(new RuntimeException("Actor thread pool terminated unexpectedly"));

        Optional<ReconnectParticipantOutput> result = useCase.execute(input);

        assertTrue(result.isEmpty());
        verify(mockUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockUser);
        verifyNoInteractions(disconnectScheduler);
    }
}