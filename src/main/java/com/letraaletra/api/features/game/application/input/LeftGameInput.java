package com.letraaletra.api.features.game.application.input;

import java.util.UUID;

public record LeftGameInput(
        UUID gameId,
        String session
) {
}
