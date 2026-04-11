package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.domain.game.Game;

public record ExpireTurnResult(
        String whoPassed,
        Game game
) {
}
