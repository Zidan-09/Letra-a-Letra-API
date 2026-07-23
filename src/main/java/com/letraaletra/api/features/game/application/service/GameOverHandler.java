package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.application.service.UpdateStatsService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

public class GameOverHandler {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final GameTimeoutManager gameTimeoutManager;
    private final UpdateStatsService updateStatsService;

    public GameOverHandler(
            GameRepository gameRepository,
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            UpdateStatsService updateStatsService
    ) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.actorManager = actorManager;
        this.gameTimeoutManager = gameTimeoutManager;
        this.updateStatsService = updateStatsService;
    }

    public void handle(Game game, GameOver result) {
        User userWinner = userRepository.find(result.winner().getUserId())
                .orElseThrow(UserNotFoundException::new);

        User userLoser = userRepository.find(result.loser().getUserId())
                .orElseThrow(UserNotFoundException::new);

        updateStatsService.execute(result.winner(), true);
        updateStatsService.execute(result.loser(), false);

        if (game.getGameType().equals(GameType.CUSTOM)) {
            game.setGameStatus(GameStatus.WAITING);
            gameTimeoutManager.start(game);
        }

        if (game.getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(game.getId());

            userWinner.leaveGame();
            userLoser.leaveGame();

            userRepository.save(userWinner);
            userRepository.save(userLoser);
        }

        gameRepository.save(game);
    }
}
