package com.letraaletra.api.features.game.domain.event;

import java.util.UUID;

public record PlayerBlindedEvent(
        UUID playerBlinded
) implements EventData {
}
