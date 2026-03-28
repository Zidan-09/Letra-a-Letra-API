package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.GameOverService;
import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.actions.GameAction;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.GameNotStartedException;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameStateResponseAssembler;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameStateUpdatedWsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerActionUseCaseTest {

    @Mock private GameRepository gameRepository;
    @Mock private UserRepository userRepository;
    @Mock private TokenService tokenService;
    @Mock private GameOverService gameOverService;
    @Mock private BroadcastService broadcast;
    @Mock private GameStateResponseAssembler gameStateResponseAssembler;

    @InjectMocks
    private PlayerActionUseCase playerActionUseCase;

    @Mock private Game game;
    @Mock private GameState gameState;
    @Mock private User user;
    @Mock private GameAction action;
    @Mock private GameStateDTO gameStateDTO;

    private final String tokenGameId = "token123";
    private final String gameId = "game123";
    private final String userId = "user1";

    @BeforeEach
    void setup() {
        lenient().when(tokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        lenient().when(gameRepository.find(gameId)).thenReturn(game);
        lenient().when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);
        lenient().when(game.getGameState()).thenReturn(gameState);
        lenient().when(gameState.getPlayerIds()).thenReturn(List.of(userId));
        lenient().when(userRepository.findMapByIds(List.of(userId))).thenReturn(Map.of(userId, user));
        lenient().when(gameStateResponseAssembler.get(gameState, Map.of(userId, user))).thenReturn(gameStateDTO);
    }

    @Test
    @DisplayName("Should execute player action successfully")
    void execute_success() {
        Mockito.when(gameOverService.buildIfFinished(game))
                        .thenReturn(Optional.empty());

        playerActionUseCase.execute(tokenGameId, userId, action);

        verify(action).execute(gameState, userId);

        verify(broadcast).send(eq(gameId), any(GameStateUpdatedWsResponse.class));
    }

    @Test
    @DisplayName("Should throw GameNotFoundException if game not found")
    void execute_gameNotFound() {
        when(gameRepository.find(gameId)).thenReturn(null);

        assertThrows(GameNotFoundException.class,
                () -> playerActionUseCase.execute(tokenGameId, userId, action)
        );
    }

    @Test
    @DisplayName("Should throw GameNotStartedException if game not running")
    void execute_gameNotRunning() {
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);

        assertThrows(GameNotStartedException.class,
                () -> playerActionUseCase.execute(tokenGameId, userId, action)
        );
    }
}