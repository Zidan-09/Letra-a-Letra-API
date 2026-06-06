package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;

public record CloseRoomOutput(
        Game game,
        String event,
        RoomCloseReasons reason
) {
}
