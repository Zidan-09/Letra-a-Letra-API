package com.letraaletra.api.features.game.domain.event;

public record PlayerUseImmunityEvent(
        String playerUseImmunity
) implements EventData {
}
