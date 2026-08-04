package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;

public class CloseGameActorCommand implements ActorCommand<Game> {
    public CloseGameActorCommand() {}

    @Override
    public Game execute(Game game) {
        game.setGameStatus(GameStatus.CANCELED);

        return game;
    }
}
