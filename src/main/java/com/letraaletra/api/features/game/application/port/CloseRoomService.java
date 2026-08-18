package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.room.CloseRoomResult;
import com.letraaletra.api.features.game.domain.Game;

public interface CloseRoomService {
    CloseRoomResult close(Game game);
}
