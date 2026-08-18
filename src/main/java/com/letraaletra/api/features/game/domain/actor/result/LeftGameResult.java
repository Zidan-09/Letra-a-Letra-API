package com.letraaletra.api.features.game.domain.actor.result;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;

import java.util.Optional;

public record LeftGameResult(
        Game game,
        Optional<GameOver> gameOver
) {}