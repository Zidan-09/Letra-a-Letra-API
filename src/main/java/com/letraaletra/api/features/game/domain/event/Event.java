package com.letraaletra.api.features.game.domain.event;

public record Event(
        StateEvent event,
        EventData data
) {
}
