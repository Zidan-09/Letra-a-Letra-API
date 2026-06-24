package com.letraaletra.api.features.game.domain.event;

import java.util.UUID;

public record PlayerAreImmuneEvent(
        UUID playerImmune
) implements EventData {
}
