package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.domain.RoomCloseReasons;

public record RoomClosed(
        String event,
        RoomCloseReasons reason
) {
}
