package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.participant.Participants;
import com.letraaletra.api.features.game.domain.participant.exception.UserNotInGameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconnectParticipantActorCommandTest {

    @Mock
    private Game game;

    @Mock
    private Participants participants;

    @Test
    @DisplayName("Should reconnect participant inside actor and return game when participant exists")
    void shouldReconnectAndReturnGameWhenParticipantExists() {
        UUID userId = UUID.randomUUID();
        String sessionId = "new-session";

        when(game.getParticipants()).thenReturn(participants);

        Optional<Game> result = new ReconnectParticipantActorCommand(userId, sessionId).execute(game);

        assertTrue(result.isPresent());
        assertEquals(game, result.get());
        verify(participants, times(1)).reconnect(userId, sessionId);
    }

    @Test
    @DisplayName("Should return empty without propagating when participant is missing")
    void shouldReturnEmptyWhenParticipantIsMissing() {
        UUID userId = UUID.randomUUID();

        when(game.getParticipants()).thenReturn(participants);
        doThrow(new UserNotInGameException()).when(participants).reconnect(eq(userId), any());

        Optional<Game> result = new ReconnectParticipantActorCommand(userId, "new-session").execute(game);

        assertTrue(result.isEmpty());
    }
}
