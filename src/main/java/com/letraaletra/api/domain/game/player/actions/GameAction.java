package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.StateEvent;

import java.util.List;
import java.util.Optional;

public interface GameAction {
    List<StateEvent> execute(GameState state, String userId);
}
