package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.util.List;

public record PlayerActionResult(
        List<StateEvent> events,
        GameOverResult gameOverResult,
        Game game
) {
}
