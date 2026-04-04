package com.letraaletra.api.application.output.player;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.util.Optional;

public record PlayerActionOutput(
        Game game,
        Optional<GameOverResult> gameOver
) {
}
