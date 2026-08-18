package com.letraaletra.api.features.game.domain.room;

import com.letraaletra.api.features.game.domain.Game;

public record CloseRoomResult(
        Game game,
        String event,
        RoomCloseReasons reason
) {
}
