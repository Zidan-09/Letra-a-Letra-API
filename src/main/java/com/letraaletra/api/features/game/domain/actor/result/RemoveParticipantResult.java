package com.letraaletra.api.features.game.domain.actor.result;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.util.Optional;

public record RemoveParticipantResult(
        Game game,
        Optional<GameOver> gameOver
) {
}
