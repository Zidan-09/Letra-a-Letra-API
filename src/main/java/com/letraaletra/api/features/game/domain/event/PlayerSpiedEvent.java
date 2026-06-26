package com.letraaletra.api.features.game.domain.event;

import java.util.UUID;

public record PlayerSpiedEvent(
        UUID spiedBy
) implements EventData {
}
