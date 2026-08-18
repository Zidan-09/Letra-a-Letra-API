package com.letraaletra.api.features.game.domain.room;

public record RoomClosed(
        String event,
        RoomCloseReasons reason
) {
}
