package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;

public interface GameOverService {
    void handle(Game game, GameOver result);
}
