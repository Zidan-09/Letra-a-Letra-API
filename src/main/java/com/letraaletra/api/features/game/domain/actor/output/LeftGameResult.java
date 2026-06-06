package com.letraaletra.api.features.game.domain.actor.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

public record LeftGameResult(
        Game game,
        String user,
        boolean isEmpty,
        GameOverResult gameOverResult
) {}