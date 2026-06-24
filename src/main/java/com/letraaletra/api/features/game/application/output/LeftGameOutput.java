package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

public record LeftGameOutput(
        Game game,
        GameOverResult gameOverResult
) {
}
