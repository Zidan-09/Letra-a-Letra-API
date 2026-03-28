package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.ThemeWordSelectorService;
import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
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
import com.letraaletra.api.presentation.dto.response.websocket.GameStartedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StartGameUseCase {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameStateGenerator gameStateGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ThemeWordSelectorService themeWordSelector;

    @Autowired
    private BoardGenerator boardGenerator;

    @Autowired
    private GameStateResponseAssembler gameStateResponseAssembler;

    @Autowired
    private BroadcastService broadcast;

    public void execute(String tokenGameId, GameSettings settings, String sessionId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);

        String hostId = game.getHostId();
        Participant participant = game.findBySession(sessionId);

        validateParticipant(participant);
        validateHost(participant, hostId);

        String themeId = settings.getThemeId();

        Theme theme = themeRepository.findById(themeId);

        List<String> words = selectWords(theme);

        GameMode gameMode = settings.getGameMode();

        Board board = boardGenerator.generate(words, gameMode);

        GameState state = gameStateGenerator.generate(game.getParticipants(), board);

        game.updateGameState(state);

        List<String> playerIds = state.getPlayerIds();

        Map<String, User> users = userRepository.findMapByIds(playerIds);

        game.setGameStatus(GameStatus.RUNNING);

        GameStartedWsResponse data = buildResponse(state, users);

        broadcast.send(gameId, data);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private List<String> selectWords(Theme theme) {
        if (theme != null) {
            return themeWordSelector.pickRandomWords(theme);
        } else {
            return themeWordSelector.pickRandomThemeWords();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateHost(Participant participant, String hostId) {
        if (!participant.getUserId().equals(hostId)) {
            throw new OnlyHostCanStartException();
        }
    }

    private GameStartedWsResponse buildResponse(GameState state, Map<String, User> users) {
        return new GameStartedWsResponse(
                gameStateResponseAssembler.get(state, users)
        );
    }
}
