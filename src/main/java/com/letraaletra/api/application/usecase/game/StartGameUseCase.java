package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.StartGameCommand;
import com.letraaletra.api.application.port.GameTimeOut;
import com.letraaletra.api.application.service.ThemeWordSelectorService;
import com.letraaletra.api.application.output.game.StartGameOutput;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.ThemeRepository;
import com.letraaletra.api.domain.game.board.theme.Theme;
import com.letraaletra.api.domain.game.participant.exception.OnlyHostCanStartException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StartGameUseCase {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameStateGenerator gameStateGenerator;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private GameTimeOut gameTimeOut;

    @Autowired
    private ThemeWordSelectorService themeWordSelector;

    @Autowired
    private BoardGenerator boardGenerator;

    public StartGameOutput execute(StartGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        gameTimeOut.cancel(game);

        String hostId = game.getHostId();
        Participant participant = game.findBySession(command.session());

        validateParticipant(participant);
        validateHost(participant, hostId);

        String themeId = command.settings().getThemeId();

        Theme theme = themeRepository.findById(themeId);

        List<String> words = selectWords(theme);

        GameMode gameMode = command.settings().getGameMode();

        Board board = boardGenerator.generate(words, gameMode);

        GameState state = gameStateGenerator.generate(game.getParticipants(), board);

        game.updateGameState(state);

        game.setGameStatus(GameStatus.RUNNING);

        gameRepository.save(game);

        return buildReturn(game, gameId);
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

    private StartGameOutput buildReturn(Game game, String id) {
        return new StartGameOutput(
                id,
                game
        );
    }
}
