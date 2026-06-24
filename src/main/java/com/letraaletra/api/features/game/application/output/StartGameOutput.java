package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;

public record StartGameOutput(
        UUID id,
        Game game
) {
}
