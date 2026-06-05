package com.letraaletra.api.application.port;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

public interface GameNotifier {
    void notifierAll(Game game, Object dto);
    void notifierOne(String userId, Object dto);
    void notifierGameOver(Game game, GameOverResult gameOverResult);
}
