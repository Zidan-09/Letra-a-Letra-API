package com.letraaletra.api.features.participant.application.service;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.exception.InvalidModerateActionException;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanModerateException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationContextServiceTest {

    @Mock
    private ActorManager<Game> actorManager;

    @InjectMocks
    private ModerationContextService service;

    private UUID gameId;
    private UUID targetId;
    private UUID hostId;

    @Mock
    private Actor mockActor;

    @Mock
    private Game mockGame;

    @Mock
    private Participant mockParticipant;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        hostId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should successfully resolve moderation context when host is valid, target is different, and participant exists in game")
    void shouldResolveModerationContextSuccessfully() {
        when(actorManager.get(gameId)).thenReturn(mockActor);
        when(mockActor.getGame()).thenReturn(mockGame);
        when(mockGame.getHostId()).thenReturn(hostId);
        when(mockGame.getParticipantByUserId(targetId)).thenReturn(mockParticipant);

        ModerationContext context = service.resolve(gameId, targetId, hostId);

        assertNotNull(context);
        assertEquals(mockGame, context.game()); // Assumindo record component ou getter .game()
        assertEquals(mockParticipant, context.participant()); // Assumindo record component ou getter .participant()

        verify(actorManager, times(1)).get(gameId);
        verify(mockGame, times(1)).getHostId();
        verify(mockGame, times(1)).getParticipantByUserId(targetId);
    }

    @Test
    @DisplayName("Should throw OnlyHostCanModerateException when the executing user is not the actual game host")
    void shouldThrowExceptionWhenUserIsNotTheHost() {
        UUID nonHostId = UUID.randomUUID();
        when(actorManager.get(gameId)).thenReturn(mockActor);
        when(mockActor.getGame()).thenReturn(mockGame);
        when(mockGame.getHostId()).thenReturn(hostId); // O host real é diferente de nonHostId

        assertThrows(OnlyHostCanModerateException.class, () -> service.resolve(gameId, targetId, nonHostId));

        verify(mockGame, never()).getParticipantByUserId(any());
    }

    @Test
    @DisplayName("Should throw InvalidModerateActionException when the host attempts to moderate themselves")
    void shouldThrowExceptionWhenHostAttemptsToModerateThemselves() {
        when(actorManager.get(gameId)).thenReturn(mockActor);
        when(mockActor.getGame()).thenReturn(mockGame);
        when(mockGame.getHostId()).thenReturn(hostId);

        assertThrows(InvalidModerateActionException.class, () -> service.resolve(gameId, hostId, hostId));

        verify(mockGame, never()).getParticipantByUserId(any());
    }

    @Test
    @DisplayName("Should throw UserNotInGameException when the target user cannot be resolved as an active participant")
    void shouldThrowExceptionWhenTargetParticipantDoesNotExist() {
        when(actorManager.get(gameId)).thenReturn(mockActor);
        when(mockActor.getGame()).thenReturn(mockGame);
        when(mockGame.getHostId()).thenReturn(hostId);
        when(mockGame.getParticipantByUserId(targetId)).thenReturn(null);

        assertThrows(UserNotInGameException.class, () -> service.resolve(gameId, targetId, hostId));
    }

    @Test
    @DisplayName("Should propagate any unexpected runtime exception thrown by the internal actor manager layer")
    void shouldPropagateExceptionsFromActorManager() {
        when(actorManager.get(gameId)).thenThrow(new RuntimeException("Actor cluster communications failure"));

        assertThrows(RuntimeException.class, () -> service.resolve(gameId, targetId, hostId));
    }
}