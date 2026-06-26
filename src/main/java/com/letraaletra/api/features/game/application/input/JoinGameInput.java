package com.letraaletra.api.features.game.application.input;

import java.util.UUID;

public record JoinGameInput(
        UUID gameId,
        String session,
        UUID user
) {
}
