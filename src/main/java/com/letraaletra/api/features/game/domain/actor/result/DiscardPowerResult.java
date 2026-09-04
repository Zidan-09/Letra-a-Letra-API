package com.letraaletra.api.features.game.domain.actor.result;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.event.Event;

import java.util.List;

public record DiscardPowerResult(
        Game game,
        List<Event> events
) {
}
