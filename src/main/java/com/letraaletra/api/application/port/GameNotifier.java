package com.letraaletra.api.application.port;

import com.letraaletra.api.domain.game.Game;

public interface GameNotifier {
    void send(Game game, Object dto);
}
