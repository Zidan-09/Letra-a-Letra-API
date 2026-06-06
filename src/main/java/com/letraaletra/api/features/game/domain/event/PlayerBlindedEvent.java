package com.letraaletra.api.features.game.domain.event;

public record PlayerBlindedEvent(
        String playerBlinded
) implements EventData {
}
