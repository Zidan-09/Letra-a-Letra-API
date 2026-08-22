package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;

import java.util.Optional;
import java.util.UUID;

public record ExpireTurnTimeoutResult(
        String event,
        UUID user,
        UUID currentPlayerTurnId,
        Game game,
        Optional<GameOver> gameOver,
        HandledGameOver handledGameOver
) {
}
