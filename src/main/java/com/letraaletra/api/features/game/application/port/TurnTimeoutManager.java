package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.Game;

public interface TurnTimeoutManager {
    void start(Game game);
}
