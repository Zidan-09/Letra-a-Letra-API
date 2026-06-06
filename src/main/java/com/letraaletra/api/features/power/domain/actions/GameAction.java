package com.letraaletra.api.features.power.domain.actions;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.state.GameState;

import java.util.List;

public interface GameAction {
    List<Event> execute(GameState state, String userId);
}
