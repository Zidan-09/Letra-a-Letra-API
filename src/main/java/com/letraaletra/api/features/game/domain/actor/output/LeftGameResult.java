package com.letraaletra.api.features.game.domain.actor.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

import java.util.UUID;

public record LeftGameResult(
        Game game,
        UUID user,
        boolean isEmpty,
        GameOverResult gameOverResult
) {}