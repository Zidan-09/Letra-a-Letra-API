package com.letraaletra.api.features.player.application.input;

public record DiscardPowerInput(
        String tokenGameId,
        String userId,
        String powerId
) {
}
