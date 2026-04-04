package com.letraaletra.api.infrastructure.manager;

import com.letraaletra.api.domain.game.RoomCloseReasons;

public record RoomClosed(
        String event,
        RoomCloseReasons reason
) {
}
