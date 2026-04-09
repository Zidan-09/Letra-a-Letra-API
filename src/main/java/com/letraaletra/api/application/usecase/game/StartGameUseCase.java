package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.StartGameCommand;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.output.game.StartGameOutput;
import com.letraaletra.api.domain.game.exception.GameIsRunningException;
import com.letraaletra.api.domain.game.exception.InsufficientPlayersException;
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

import java.util.List;
import java.util.Random;

public class StartGameUseCase {
    private final GameRepository gameRepository;
    private final GameStateGenerator gameStateGenerator;
    private final ThemeRepository themeRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase;
    private final BoardGenerator boardGenerator;
    private final TokenService tokenService;

    public StartGameUseCase(
            GameRepository gameRepository,
            GameStateGenerator gameStateGenerator,
            ThemeRepository themeRepository,
            GameTimeoutManager gameTimeoutManager,
            PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
            BoardGenerator boardGenerator,
            TokenService tokenService
    ) {
        this.gameRepository = gameRepository;
        this.gameStateGenerator = gameStateGenerator;
        this.themeRepository = themeRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.pickRandomThemeWordsUseCase = pickRandomThemeWordsUseCase;
        this.boardGenerator = boardGenerator;
        this.tokenService = tokenService;
    }

    public StartGameOutput execute(StartGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        gameTimeoutManager.cancel(game);

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

        return buildOutput(game, gameId);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus() == GameStatus.RUNNING) {
            throw new GameIsRunningException();
        }

        if (game.getAmountPlayers() < 2) {
            throw new InsufficientPlayersException();
        }
    }

    private List<String> selectWords(Theme theme) {
        if (theme != null) {
            return theme.pickRandomWords(5, new Random());
        } else {
            return pickRandomThemeWordsUseCase.execute();
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

    private StartGameOutput buildOutput(Game game, String id) {
        return new StartGameOutput(
                id,
                game
        );
    }
}
