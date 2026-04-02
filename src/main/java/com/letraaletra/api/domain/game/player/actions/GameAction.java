package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.GameState;

public interface GameAction {
    void execute(GameState state, String userId);
}
