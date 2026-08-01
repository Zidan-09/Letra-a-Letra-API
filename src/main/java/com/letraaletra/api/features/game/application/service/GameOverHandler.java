package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.application.service.UpdateStatsService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

import java.util.List;

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
        List<User> userList = userRepository.findUsersById(List.of(
                result.winner().getUserId(),
                result.loser().getUserId()
        ));

        User userWinner = userList.stream()
                .filter(u -> u.getId().equals(result.winner().getUserId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        User userLoser = userList.stream()
                .filter(u -> u.getId().equals(result.loser().getUserId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        updateStatsService.execute(userWinner, true);
        updateStatsService.execute(userLoser, false);

        if (game.getGameType().equals(GameType.CUSTOM)) {
            game.setGameStatus(GameStatus.WAITING);
            gameTimeoutManager.start(game);
        } else {
            game.setGameStatus(GameStatus.CLOSED);
        }

        if (game.getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(game.getId());
        }

        userRepository.saveAll(List.of(userWinner, userLoser));
        gameRepository.save(game);
    }
}
