package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.websocket.BroadcastService;
import com.letraaletra.api.presentation.mappers.game.GameStateResponseAssembler;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisconnectManagerTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameStateResponseAssembler assembler;

    @Mock
    private BroadcastService broadcast;

    @InjectMocks
    private DisconnectManager disconnectManager;

    private Method handleTimeoutMethod;

    private static final String USER_ID = "user-1";
    private static final String GAME_ID = "game-1";

    @BeforeEach
    void setup() throws Exception {
        handleTimeoutMethod = DisconnectManager.class
                .getDeclaredMethod("handleTimeout", String.class, String.class);
        handleTimeoutMethod.setAccessible(true);
    }

    private void invokeTimeout() throws Exception {
        handleTimeoutMethod.invoke(disconnectManager, DisconnectManagerTest.USER_ID, DisconnectManagerTest.GAME_ID);
    }

    @Test
    @DisplayName("Should do nothing when game or user is null")
    void gameOrUserIsNull() throws Exception {
        when(gameRepository.find(GAME_ID)).thenReturn(null);
        when(userRepository.find(USER_ID)).thenReturn(null);

        invokeTimeout();

        verifyNoInteractions(broadcast);
    }

    @Test
    @DisplayName("Should do nothing when Participant not found")
    void participantNotFound() throws Exception {
        Game game = mock(Game.class);
        User user = mock(User.class);

        when(gameRepository.find(GAME_ID)).thenReturn(game);
        when(userRepository.find(USER_ID)).thenReturn(user);
        when(game.getParticipantByUserId(USER_ID)).thenReturn(null);

        invokeTimeout();

        verifyNoInteractions(broadcast);
    }

    @Test
    @DisplayName("Should do nothing when Participant still connected")
    void participantConnected() throws Exception {
        Game game = mock(Game.class);
        User user = mock(User.class);
        Participant participant = mock(Participant.class);

        when(gameRepository.find(GAME_ID)).thenReturn(game);
        when(userRepository.find(USER_ID)).thenReturn(user);
        when(game.getParticipantByUserId(USER_ID)).thenReturn(participant);
        when(participant.isConnected()).thenReturn(true);

        invokeTimeout();

        verifyNoInteractions(broadcast);
    }

    @Test
    @DisplayName("Should remove Player and Broadcast when disconnected")
    void execute() throws Exception {
        Game game = mock(Game.class);
        User user = mock(User.class);
        Participant participant = mock(Participant.class);
        GameState state = mock(GameState.class);

        when(gameRepository.find(GAME_ID)).thenReturn(game);
        when(userRepository.find(USER_ID)).thenReturn(user);
        when(game.getParticipantByUserId(USER_ID)).thenReturn(participant);
        when(participant.isConnected()).thenReturn(false);

        when(game.getGameState()).thenReturn(state);
        when(state.getPlayerIds()).thenReturn(List.of(USER_ID));

        when(userRepository.findMapByIds(List.of(USER_ID)))
                .thenReturn(Map.of(USER_ID, user));

        when(assembler.get(eq(state), any()))
                .thenReturn(mock(GameStateDTO.class));

        invokeTimeout();

        verify(game).remove(USER_ID);
        verify(user).leaveGame();

        verify(gameRepository).save(game);
        verify(userRepository).save(user);

        verify(broadcast).send(eq(GAME_ID), any());
    }

    @Test
    @DisplayName("Should cancel existing timer")
    void cancelTimer() {
        disconnectManager.start(USER_ID, GAME_ID);
        disconnectManager.cancel(USER_ID, GAME_ID);
    }
}