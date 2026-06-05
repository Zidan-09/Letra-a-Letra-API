package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

public record ExpireTurnResult(
        String whoPassed,
        Game game,
        GameOverResult gameOverResult,
        boolean removedBecauseAfk
) {
}
