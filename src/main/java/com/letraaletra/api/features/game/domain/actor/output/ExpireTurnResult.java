package com.letraaletra.api.features.game.domain.actor.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.util.Optional;
import java.util.UUID;

public record ExpireTurnResult(
        UUID whoPassed,
        Game game,
        Optional<GameOver> gameOver
) {
}
