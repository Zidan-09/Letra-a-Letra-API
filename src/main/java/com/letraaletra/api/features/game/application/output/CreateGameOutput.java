package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;

public record CreateGameOutput(
        String token,
        Game game
) {
}
