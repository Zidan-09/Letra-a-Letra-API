package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.CloseRoomResult;
import com.letraaletra.api.features.game.domain.Game;

public interface CloseRoomService {
    CloseRoomResult close(Game game);
}
