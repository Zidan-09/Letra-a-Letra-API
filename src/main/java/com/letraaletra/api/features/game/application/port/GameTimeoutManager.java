package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.Game;

public interface GameTimeoutManager {
    void start(Game game);
    void cancel(Game game);
}
