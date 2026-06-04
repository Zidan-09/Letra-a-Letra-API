package com.letraaletra.api.features.player.application.output;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.util.List;
import java.util.Optional;

public record PlayerActionOutput(
        Game game,
        List<Event> events,
        Optional<GameOverResult> gameOver
) {
}
