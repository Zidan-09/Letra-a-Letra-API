package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;

public record ExpireTurnOutput(
        String event,
        String user,
        String currentPlayerTurnId,
        Game game
) {
}
