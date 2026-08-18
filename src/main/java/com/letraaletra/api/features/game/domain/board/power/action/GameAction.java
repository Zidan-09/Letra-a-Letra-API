package com.letraaletra.api.features.game.domain.board.power.action;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.state.GameState;

import java.util.List;
import java.util.UUID;

public interface GameAction {
    List<Event> execute(GameState state, UUID userId);
}
