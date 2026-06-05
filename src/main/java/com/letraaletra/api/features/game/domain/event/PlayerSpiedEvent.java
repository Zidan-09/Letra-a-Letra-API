package com.letraaletra.api.features.game.domain.event;

public record PlayerSpiedEvent(
        String spiedBy
) implements EventData {
}
