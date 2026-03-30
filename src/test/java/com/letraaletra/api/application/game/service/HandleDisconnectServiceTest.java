package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.presentation.websocket.PlayerGameContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandleDisconnectServiceTest {

    @Mock
    private DisconnectManager disconnectManager;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerGameContextResolver playerGameContextResolver;

    @InjectMocks
    private HandleDisconnectService handleDisconnectService;

    private WebSocketSession session;

    @BeforeEach
    void setup() {
        session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(Map.of("userId", "user-1"));
    }

    @Test
    @DisplayName("Should do nothing when context is null")
    void contextIsNull() {
        when(playerGameContextResolver.resolve("user-1")).thenReturn(null);

        handleDisconnectService.execute(session);

        verifyNoInteractions(disconnectManager);
        verifyNoInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should handle disconnect correctly")
    void execute() {
        Participant participant = mock(Participant.class);
        Game game = mock(Game.class);

        when(game.getId()).thenReturn("game-1");

        PlayerGameContext ctx = new PlayerGameContext(null, game, participant);

        when(playerGameContextResolver.resolve("user-1")).thenReturn(ctx);

        handleDisconnectService.execute(session);

        verify(disconnectManager, times(1)).start("user-1", "game-1");
        verify(participant, times(1)).disconnect();
        verify(gameRepository, times(1)).save(game);

        verifyNoMoreInteractions(disconnectManager, gameRepository, participant);
    }
}