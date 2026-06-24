package com.letraaletra.api.features.game.domain.event;

import java.util.UUID;

public record PlayerUseImmunityEvent(
        UUID playerUseImmunity
) implements EventData {
}
