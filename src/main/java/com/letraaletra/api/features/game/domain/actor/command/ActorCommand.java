package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;

public interface ActorCommand<T> {
    T execute(Game game);
}
