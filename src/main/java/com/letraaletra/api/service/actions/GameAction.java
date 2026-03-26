package com.letraaletra.api.service.actions;

import com.letraaletra.api.domain.Game;

public interface GameAction {
    void execute(Game game, String userId);
}
