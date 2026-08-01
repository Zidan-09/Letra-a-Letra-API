package com.letraaletra.api.features.game.domain;

public record CloseRoomResult(
        Game game,
        String event,
        RoomCloseReasons reason
) {
}
