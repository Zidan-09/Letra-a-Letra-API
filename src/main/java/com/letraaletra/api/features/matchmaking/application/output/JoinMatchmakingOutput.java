package com.letraaletra.api.features.matchmaking.application.output;

import com.letraaletra.api.features.game.domain.Game;

import java.util.Optional;

public record JoinMatchmakingOutput(
    Optional<Game> game
) {
}
