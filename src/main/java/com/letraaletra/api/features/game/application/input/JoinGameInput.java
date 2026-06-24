package com.letraaletra.api.features.game.application.input;

import java.util.UUID;

public record JoinGameInput(
        String token,
        String session,
        UUID user
) {
}
