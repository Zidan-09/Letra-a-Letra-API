package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.User;

public class JoinGameActorCommand implements ActorCommand<Game> {
    private final User user;
    private final String session;

    public JoinGameActorCommand(User user, String session) {
        this.user = user;
        this.session = session;
    }

    @Override
    public Game execute(Game game) {

        game.join(user, session);
        user.enterGame(game.getId());

        return game;
    }
}
