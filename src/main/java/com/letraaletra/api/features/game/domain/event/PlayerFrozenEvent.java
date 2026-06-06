package com.letraaletra.api.features.game.domain.event;

public record PlayerFrozenEvent(
        String playerFrozen
) implements EventData {
}
