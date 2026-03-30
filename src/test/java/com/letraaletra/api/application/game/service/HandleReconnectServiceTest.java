package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.mappers.GameStateResponseAssembler;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameStateUpdatedWsResponse;
import com.letraaletra.api.presentation.websocket.PlayerGameContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandleReconnectServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private DisconnectManager disconnectManager;

    @Mock
    private GameStateResponseAssembler gameStateResponseAssembler;

    @Mock
    private PlayerGameContextResolver playerGameContextResolver;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private HandleReconnectService handleReconnectService;

    private WebSocketSession session;

    @BeforeEach
    void setup() {
        session = mock(WebSocketSession.class);
        lenient().when(session.getAttributes()).thenReturn(Map.of("userId", "user-1"));
        lenient().when(session.getId()).thenReturn("session-1");
    }

    @Test
    @DisplayName("Should do nothing when context is null")
    void contextIsNull() {
        when(playerGameContextResolver.resolve("user-1")).thenReturn(null);

        handleReconnectService.execute(session);

        verifyNoInteractions(disconnectManager, gameRepository, userRepository, gameStateResponseAssembler);
    }

    @Test
    @DisplayName("Should handle reconnect correctly")
    void execute() throws IOException {
        Game game = mock(Game.class);
        GameState gameState = mock(GameState.class);

        when(game.getId()).thenReturn("game-1");
        when(game.getGameState()).thenReturn(gameState);

        PlayerGameContext ctx = new PlayerGameContext(null, game, null);

        when(playerGameContextResolver.resolve("user-1")).thenReturn(ctx);

        when(gameState.getPlayerIds()).thenReturn(List.of("user-1"));
        when(userRepository.findMapByIds(List.of("user-1"))).thenReturn(Map.of("user-1", mock(User.class)));

        when(gameStateResponseAssembler.get(eq(gameState), any())).thenReturn(mock(GameStateDTO.class));

        when(objectMapper.writeValueAsString(any())).thenReturn("{json}");

        handleReconnectService.execute(session);

        verify(disconnectManager).cancel("user-1", "game-1");
        verify(game).reconnect("user-1", "session-1");
        verify(gameRepository).save(game);

        verify(userRepository).findMapByIds(List.of("user-1"));
        verify(gameStateResponseAssembler).get(eq(gameState), any());

        verify(objectMapper).writeValueAsString(any(GameStateUpdatedWsResponse.class));
        verify(session).sendMessage(any(TextMessage.class));

        verifyNoMoreInteractions(disconnectManager, gameRepository, userRepository, gameStateResponseAssembler);
    }
}