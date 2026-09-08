package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameOverFinalizerService implements GameOverFinalizer {
    private final GameOverService gameOverService;
    private final GameRepository gameRepository;
    private final ActorManager<Game> actorManager;
    private final RoomTimeoutManager roomTimeoutManager;

    @Override
    public HandledGameOver finish(Game game, GameOver gameOver) {
        HandledGameOver handled = gameOverService.handle(game, gameOver);

        if (game.getGameStatus().equals(GameStatus.WAITING)) {
            roomTimeoutManager.start(game);
        } else if (game.getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(game.getId());
        }

        gameRepository.save(game);

        return handled;
    }
}
