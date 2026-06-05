package com.letraaletra.api.features.game.domain.event;

public record PlayerUnfreezeEvent(
        String playerUnfreeze
) implements EventData {
}
