package com.letraaletra.api.features.game.application.input;

public record JoinGameInput(
        String token,
        String session,
        String user
) {
}
