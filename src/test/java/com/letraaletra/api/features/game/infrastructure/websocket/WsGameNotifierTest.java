package com.letraaletra.api.features.game.infrastructure.websocket;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WsGameNotifier - semântica do GameNotifier sobre o kernel")
class WsGameNotifierTest {

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private Game game;

    private WsGameNotifier notifier;

    @BeforeEach
    void setUp() {
        notifier = new WsGameNotifier(messageSender);
    }

    private Game gameWithSockets(String... socketIds) {
        var participants =
                mock(com.letraaletra.api.features.game.domain.participant.Participants.class);
        var builder = new java.util.ArrayList<com.letraaletra.api.features.participant.domain.Participant>();
        for (String socketId : socketIds) {
            var participant = mock(com.letraaletra.api.features.participant.domain.Participant.class);
            lenient().when(participant.getSocketId()).thenReturn(socketId);
            builder.add(participant);
        }
        when(participants.getParticipants()).thenReturn(builder);

        Game game = mock(Game.class);
        when(game.getParticipants()).thenReturn(participants);
        return game;
    }

    @Test
    @DisplayName("notifierAll extrai os socketIds dos participantes e delega ao sender")
    void shouldBroadcastBySocketIds() {
        Game game = gameWithSockets("s1", "s2");

        notifier.notifierAll(game, new Object());

        verify(messageSender).sendToSessions(eq(List.of("s1", "s2")), any());
    }

    @Test
    @DisplayName("notifierAll com game nulo lança GameNotFoundException")
    void shouldThrowForNullGame() {
        assertThrows(GameNotFoundException.class, () -> notifier.notifierAll(null, new Object()));

        verifyNoInteractions(messageSender);
    }

    @Test
    @DisplayName("notifierOne delega para envio por userId")
    void shouldSendUnicastByUserId() {
        java.util.UUID userId = java.util.UUID.randomUUID();

        notifier.notifierOne(userId, new Object());

        verify(messageSender).sendToUser(eq(userId), any());
    }

    @Test
    @DisplayName("notifierGameOver usa o mesmo caminho de broadcast da sala")
    void shouldDelegateGameOverToBroadcastPath() {
        Game game = gameWithSockets("only");

        notifier.notifierGameOver(game, new Object());

        verify(messageSender).sendToSessions(eq(List.of("only")), any());
    }
}
