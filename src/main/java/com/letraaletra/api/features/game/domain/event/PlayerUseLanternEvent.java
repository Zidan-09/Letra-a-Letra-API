package com.letraaletra.api.features.game.domain.event;

public record PlayerUseLanternEvent(
        String playerUseLantern
) implements EventData {
}
