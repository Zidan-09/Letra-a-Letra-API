package com.letraaletra.api.application.output.player;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.util.List;
import java.util.Optional;

public record PlayerActionOutput(
        Game game,
        List<StateEvent> event,
        Optional<GameOverResult> gameOver
) {
}
