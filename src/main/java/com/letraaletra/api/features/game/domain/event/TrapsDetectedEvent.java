package com.letraaletra.api.features.game.domain.event;

public record TrapsDetectedEvent(
        String detectedBy
) implements EventData {
}
