package com.letraaletra.api.features.player.application.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

import java.util.List;
import java.util.Optional;

public record PlayerActionOutput(
        Game game,
        List<Event> events,
        Optional<GameOverResult> gameOver
) {
}
