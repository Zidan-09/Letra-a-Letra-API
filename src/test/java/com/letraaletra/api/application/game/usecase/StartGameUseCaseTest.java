package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.ThemeWordSelectorService;
import com.letraaletra.api.application.game.service.TimeoutManager;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.*;
import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.ThemeRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.game.exceptions.OnlyHostCanStartException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameStateResponseAssembler;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameStartedWsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartGameUseCaseTest {

    @Mock private GlobalTokenService globalTokenService;
    @Mock private GameRepository gameRepository;
    @Mock private GameStateGenerator gameStateGenerator;
    @Mock private UserRepository userRepository;
    @Mock private ThemeRepository themeRepository;
    @Mock private ThemeWordSelectorService themeWordSelector;
    @Mock private BoardGenerator boardGenerator;
    @Mock private GameStateResponseAssembler gameStateResponseAssembler;
    @Mock private BroadcastService broadcast;

    @InjectMocks
    private StartGameUseCase startGameUseCase;

    private final String tokenGameId = "token123";
    private final String gameId = "game123";
    private final String sessionId = "session1";

    @Mock private Game game;
    @Mock private Participant participant;
    @Mock private Theme theme;
    @Mock private Board board;
    @Mock private GameState gameState;
    @Mock private User user;
    @Mock private GameStateDTO gameStateDTO;
    @Mock private TimeoutManager timeoutManager;

    @BeforeEach
    void setup() {
        lenient().when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        lenient().when(gameRepository.find(gameId)).thenReturn(game);

        lenient().when(game.findBySession(sessionId)).thenReturn(participant);
        String hostId = "userHost";
        lenient().when(participant.getUserId()).thenReturn(hostId);
        lenient().when(game.getHostId()).thenReturn(hostId);

        lenient().when(game.getParticipants()).thenReturn(List.of(participant));
        GameSettings settings = mock(GameSettings.class);
        lenient().when(settings.getThemeId()).thenReturn("theme123");
        lenient().when(settings.getGameMode()).thenReturn(GameMode.NORMAL);

        lenient().when(themeRepository.findById("theme123")).thenReturn(theme);
        lenient().when(boardGenerator.generate(anyList(), eq(GameMode.NORMAL))).thenReturn(board);

        lenient().when(boardGenerator.generate(List.of("word1", "word2"), GameMode.NORMAL)).thenReturn(board);
        lenient().when(gameStateGenerator.generate(anyList(), eq(board))).thenReturn(gameState);
        lenient().when(gameState.getPlayerIds()).thenReturn(List.of(hostId));

        lenient().when(userRepository.findMapByIds(List.of(hostId))).thenReturn(Map.of(hostId, user));

        lenient().when(gameStateResponseAssembler.get(gameState, Map.of(hostId, user))).thenReturn(gameStateDTO);
    }

    @Test
    @DisplayName("Should start game successfully")
    void execute_success() {
        GameSettings settings = new GameSettings("tech", GameMode.NORMAL);

        startGameUseCase.execute(tokenGameId, settings, sessionId);

        verify(game).updateGameState(gameState);
        verify(broadcast).send(eq(gameId), any(GameStartedWsResponse.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException if participant not found")
    void execute_participantNotFound() {
        GameSettings settings = new GameSettings("tech", GameMode.NORMAL);

        when(game.findBySession(sessionId)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> startGameUseCase.execute(tokenGameId, settings, sessionId)
        );
    }

    @Test
    @DisplayName("Should throw OnlyHostCanStartException if participant is not host")
    void execute_notHost() {
        GameSettings settings = new GameSettings("tech", GameMode.NORMAL);

        when(participant.getUserId()).thenReturn("otherUser");

        assertThrows(OnlyHostCanStartException.class,
                () -> startGameUseCase.execute(tokenGameId, settings, sessionId)
        );
    }
}