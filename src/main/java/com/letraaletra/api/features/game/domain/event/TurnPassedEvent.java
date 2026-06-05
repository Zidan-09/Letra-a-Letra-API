package com.letraaletra.api.features.game.domain.event;

public record TurnPassedEvent(
        String passedBy
) implements EventData {
}
