package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;

public interface GameOverService {
    HandledGameOver handle(Game game, GameOver result);
}
