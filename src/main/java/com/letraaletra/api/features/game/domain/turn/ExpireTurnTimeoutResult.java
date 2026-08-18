package com.letraaletra.api.features.game.domain.turn;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;

import java.util.Optional;
import java.util.UUID;

public record ExpireTurnTimeoutResult(
        String event,
        UUID user,
        UUID currentPlayerTurnId,
        Game game,
        Optional<GameOver> gameOver
) {
}
