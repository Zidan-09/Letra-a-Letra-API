package com.letraaletra.api.application.port;

import com.letraaletra.api.domain.game.Game;

public interface GameNotifier {
    void notifierAll(Game game, Object dto);
    void notifierOne(String userId, Object dto);
}
