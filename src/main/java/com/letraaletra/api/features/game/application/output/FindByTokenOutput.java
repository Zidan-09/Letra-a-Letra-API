package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;

public record FindByTokenOutput(
        String token,
        Game game
) {
}
