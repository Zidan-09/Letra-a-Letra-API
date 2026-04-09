package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;

import java.util.Optional;

public record JoinMatchmakingOutput(
    Optional<Game> game
) {
}
