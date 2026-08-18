package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;

public class SwapPositionActorCommand implements ActorCommand<Game> {
    private final UUID user;
    private final int position;

    public SwapPositionActorCommand(UUID user, int position) {
        this.user = user;
        this.position = position;
    }

    @Override
    public Game execute(Game game) {
        game.changePosition(user, position);

        return game;
    }
}
