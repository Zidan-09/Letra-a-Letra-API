package com.letraaletra.api.features.game.domain.actor.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.util.List;
import java.util.Optional;

public record PlayerActionResult(
        List<Event> events,
        Optional<GameOver> gameOver,
        Game game
) {
}
