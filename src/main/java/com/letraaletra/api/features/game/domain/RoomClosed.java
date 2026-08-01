package com.letraaletra.api.features.game.domain;

public record RoomClosed(
        String event,
        RoomCloseReasons reason
) {
}
