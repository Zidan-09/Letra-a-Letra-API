package com.letraaletra.api.features.game.domain.event;

public record PlayerAreImmuneEvent(
        String playerImmune
) implements EventData {
}
