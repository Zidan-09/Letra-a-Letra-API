package com.letraaletra.api.features.player.application.input;

import java.util.UUID;

public record DiscardPowerInput(
        UUID gameId,
        UUID userId,
        String powerId
) {
}
