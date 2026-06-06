package com.letraaletra.api.features.game.infrastructure.scheduler;

public record TurnExpired(
        String event,
        ExpiredData data
) {
    public record ExpiredData(
            String user,
            String currentTurnPlayerId
    ) {}
}
