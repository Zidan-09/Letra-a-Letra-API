package com.letraaletra.api.application.port;

import com.letraaletra.api.domain.game.Game;

public interface GameTimeOut {
    void start(Game game);
    void cancel(Game game);
}
