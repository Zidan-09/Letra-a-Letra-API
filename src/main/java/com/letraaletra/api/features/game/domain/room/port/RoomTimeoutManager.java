package com.letraaletra.api.features.game.domain.room.port;

import com.letraaletra.api.features.game.domain.Game;

public interface RoomTimeoutManager {
    void start(Game game);
    void cancel(Game game);
}
