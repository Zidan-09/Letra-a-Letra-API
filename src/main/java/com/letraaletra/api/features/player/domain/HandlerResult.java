package com.letraaletra.api.features.player.domain;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.util.Optional;

public record HandlerResult(
        Game game,
        Optional<GameOver> gameOver
) {
}
