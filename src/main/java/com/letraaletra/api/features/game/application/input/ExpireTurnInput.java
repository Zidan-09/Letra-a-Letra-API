package com.letraaletra.api.features.game.application.input;

public record ExpireTurnInput(
        String gameId,
        int version
) {
}
