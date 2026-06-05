package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.features.game.domain.Game;

public interface ActorCommand<T> {
    T execute(Game game);
}
