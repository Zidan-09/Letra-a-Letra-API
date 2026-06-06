package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.application.service.UpdateStatsService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;

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

    public void handle(Game game, GameOverResult result) {
        if (!result.finished()) return;

        updateStatsService.execute(result.winner(), true);
        updateStatsService.execute(result.loser(), false);

        if (game.getGameType().equals(GameType.CUSTOM)) {
            game.setGameStatus(GameStatus.WAITING);
            gameTimeoutManager.start(game);
        }

        if (game.getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(game.getId());
            removeAllParticipantsFromGame(game);
        }

        gameRepository.save(game);
    }

    private void removeAllParticipantsFromGame(Game game) {
        game.getParticipants().forEach(participant -> {
                    Optional<User> user = userRepository.find(participant.getUserId());

                    if (user.isPresent()) {
                        user.get().leaveGame();

                        userRepository.save(user.get());
                    }
                }
        );
    }
}
