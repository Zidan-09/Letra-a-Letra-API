package com.letraaletra.api.features.participant.application.output;

import com.letraaletra.api.features.game.domain.Game;

public record SwapPositionOutput(
        String token,
        Game game
) {
}
