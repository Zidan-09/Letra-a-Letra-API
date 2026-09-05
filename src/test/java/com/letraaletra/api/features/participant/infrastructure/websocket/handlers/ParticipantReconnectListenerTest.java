package com.letraaletra.api.features.participant.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.generator.BoardGenerator;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.application.port.ParticipantNotifier;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ParticipantReconnectListenerTest {

    @Mock
    private UseCase<ReconnectParticipantInput, Optional<ReconnectParticipantOutput>> useCase;

    @Mock
    private ParticipantNotifier notifier;

    @Mock
    private WebSocketSession session;

    private ParticipantReconnectListener listener;

    @BeforeEach
    void setUp() {
        listener = new ParticipantReconnectListener(useCase, notifier);
    }

    private User mockUser(UUID id) {
        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User u = mock(User.class);
        lenient().when(u.getUserId()).thenReturn(id);
        lenient().when(u.getUsername()).thenReturn("u-" + id.toString().substring(0,4));
        lenient().when(u.getInventory()).thenReturn(inv);
        return u;
    }

    private Game createWaitingGameWithParticipants() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE", "room", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User u1 = mockUser(UUID.randomUUID());
        User u2 = mockUser(UUID.randomUUID());
        game.join(u1, "s1");
        game.join(u2, "s2");
        return game;
    }

    private Game createRunningGameWithParticipants() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE", "room", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User u1 = mockUser(UUID.randomUUID());
        User u2 = mockUser(UUID.randomUUID());
        game.join(u1, "s1");
        game.join(u2, "s2");
        Board board = BoardGenerator.generate(List.of("alpha","bravo","charlie","delta","echo"), GameMode.NORMAL);
        game.start(board);
        return game;
    }

    @Test
    @DisplayName("should notify with lobby when WAITING")
    void shouldNotifyWithLobbyWhenWaiting() {
        UUID userId = UUID.randomUUID();
        Game game = createWaitingGameWithParticipants();
        // make sure userId corresponds to participant in game
        User participantUser = mockUser(userId);
        // we need to replace game participants to include our userId, easier: create game with specific user
        // create new game with desired userId as participant
        RoomSettings settings = new RoomSettings(true, false);
        Game specificGame = Game.create("CODEW", "roomW", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User uHost = mockUser(userId);
        User uOther = mockUser(UUID.randomUUID());
        specificGame.join(uHost, "oldSocket");
        specificGame.join(uOther, "otherSocket");

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("userId", userId.toString());
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("newSocketId");
        when(useCase.execute(any())).thenReturn(Optional.of(new ReconnectParticipantOutput(specificGame)));

        assertDoesNotThrow(() -> listener.onConnected(session));

        verify(notifier, times(1)).notifyAll(anyList(), any());
    }

    @Test
    @DisplayName("should notify with gameState when RUNNING")
    void shouldNotifyWithGameStateWhenRunning() {
        UUID userId = UUID.randomUUID();
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODER", "roomR", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User uHost = mockUser(userId);
        User uOther = mockUser(UUID.randomUUID());
        game.join(uHost, "oldSocket");
        game.join(uOther, "otherSocket");
        Board board = BoardGenerator.generate(List.of("alpha","bravo","charlie","delta","echo"), GameMode.NORMAL);
        game.start(board);

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("userId", userId.toString());
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("newSocketId2");
        when(useCase.execute(any())).thenReturn(Optional.of(new ReconnectParticipantOutput(game)));

        assertDoesNotThrow(() -> listener.onConnected(session));

        verify(notifier, times(1)).notifyAll(anyList(), any());
        // capture dto to verify room and gameState
        var captor = org.mockito.ArgumentCaptor.forClass(Object.class);
        verify(notifier).notifyAll(anyList(), captor.capture());
        Object dto = captor.getValue();
        assertNotNull(dto);
        // dto is ReconnectParticipantResponse, check via reflection or instanceof
        assertTrue(dto.getClass().getSimpleName().equals("ReconnectParticipantResponse"));
        // via map, we can check that gameState is not null by inspecting record components
        try {
            var roomMethod = dto.getClass().getMethod("room");
            var gsMethod = dto.getClass().getMethod("gameState");
            Object room = roomMethod.invoke(dto);
            Object gs = gsMethod.invoke(dto);
            assertNotNull(room);
            assertNotNull(gs);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("should do nothing when output empty")
    void shouldDoNothingWhenEmpty() {
        UUID userId = UUID.randomUUID();
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("userId", userId.toString());
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("sid");
        when(useCase.execute(any())).thenReturn(Optional.empty());

        listener.onConnected(session);

        verify(notifier, never()).notifyAll(any(), any());
    }

    @Test
    @DisplayName("should handle null userId gracefully")
    void shouldHandleNullUserId() {
        Map<String, Object> attrs = new HashMap<>();
        // no userId
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("sid");

        assertDoesNotThrow(() -> listener.onConnected(session));
        verifyNoInteractions(useCase);
        verifyNoInteractions(notifier);
    }

    @Test
    @DisplayName("should not throw when useCase throws")
    void shouldNotThrowWhenUseCaseThrows() {
        UUID userId = UUID.randomUUID();
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("userId", userId.toString());
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("sid");
        when(useCase.execute(any())).thenThrow(new RuntimeException("boom"));

        assertDoesNotThrow(() -> listener.onConnected(session));
        verify(notifier, never()).notifyAll(any(), any());
    }

    @Test
    @DisplayName("should not throw even when RUNNING with null state (invariant violation) - logs warn")
    void shouldNotThrowWhenInvariantViolation() {
        UUID userId = UUID.randomUUID();
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODEINV", "roomInv", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        User uHost = mockUser(userId);
        User uOther = mockUser(UUID.randomUUID());
        game.join(uHost, "oldSocket");
        game.join(uOther, "otherSocket");
        game.setGameStatus(GameStatus.RUNNING);
        // gameState remains null -> invariant violation

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("userId", userId.toString());
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("newSocket");
        when(useCase.execute(any())).thenReturn(Optional.of(new ReconnectParticipantOutput(game)));

        assertDoesNotThrow(() -> listener.onConnected(session));
        // should not notify because mapper throws DomainException which is caught and logged, not propagated
        verify(notifier, never()).notifyAll(anyList(), any());
    }
}
