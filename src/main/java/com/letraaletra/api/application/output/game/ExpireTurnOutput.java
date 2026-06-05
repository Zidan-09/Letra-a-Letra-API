package com.letraaletra.api.application.output.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

public record ExpireTurnOutput(
        String event,
        String user,
        String currentPlayerTurnId,
        Game game,
        GameOverResult gameOverResult,
        boolean removedBecauseAfk
) {
}
