package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

import java.util.List;

public record PlayerActionResult(
        List<Event> events,
        GameOverResult gameOverResult,
        Game game
) {
}
