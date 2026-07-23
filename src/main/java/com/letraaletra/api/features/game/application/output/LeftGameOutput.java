package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.util.Optional;

public record LeftGameOutput(
        Game game,
        Optional<GameOver> gameOver
) {
}
