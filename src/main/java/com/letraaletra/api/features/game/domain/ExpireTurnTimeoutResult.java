package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.service.GameOver;

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
