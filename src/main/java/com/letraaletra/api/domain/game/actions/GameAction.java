package com.letraaletra.api.domain.game.actions;

import com.letraaletra.api.domain.GameState;

public interface GameAction {
    void execute(GameState state, String userId);
}
