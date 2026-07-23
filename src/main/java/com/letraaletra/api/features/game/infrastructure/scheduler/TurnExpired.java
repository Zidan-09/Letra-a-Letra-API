package com.letraaletra.api.features.game.infrastructure.scheduler;

import java.util.UUID;

public record TurnExpired(
        String event,
        ExpiredData data
) {
    public record ExpiredData(
            UUID user,
            UUID currentTurnPlayerId
    ) {}
}
