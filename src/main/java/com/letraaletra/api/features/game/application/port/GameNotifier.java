package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

import java.util.UUID;

public interface GameNotifier {
    void notifierAll(Game game, Object dto);
    void notifierOne(UUID userId, Object dto);
    void notifierGameOver(Game game, GameOverResult gameOverResult);
}
