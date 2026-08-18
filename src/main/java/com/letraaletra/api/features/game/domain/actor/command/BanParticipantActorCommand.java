package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public class BanParticipantActorCommand implements ActorCommand<Game> {
    private final User target;
    private final UUID host;

    public BanParticipantActorCommand(User target, UUID host) {
        this.target = target;
        this.host = host;
    }

    @Override
    public Game execute(Game game) {
        game.banParticipant(host, target.getUserId());

        target.leaveGame();

        return game;
    }
}
