package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.EquippedCosmetic;
import com.letraaletra.api.features.user.domain.User;

import java.util.List;

public class JoinGameActorCommand implements ActorCommand<Game> {
    private final User user;
    private final String session;
    private final List<EquippedCosmetic> equipped;

    public JoinGameActorCommand(User user, String session) {
        this(user, session, List.of());
    }

    public JoinGameActorCommand(User user, String session, List<EquippedCosmetic> equipped) {
        this.user = user;
        this.session = session;
        this.equipped = equipped == null ? List.of() : List.copyOf(equipped);
    }

    @Override
    public Game execute(Game game) {

        game.join(user, session, equipped);
        user.enterGame(game.getId());

        return game;
    }
}
