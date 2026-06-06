package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanModerateException;

public class BanParticipantActorCommand implements ActorCommand<Game> {
    private final String user;
    private final String host;

    public BanParticipantActorCommand(String user, String host) {
        this.user = user;
        this.host = host;
    }

    @Override
    public Game execute(Game game) {
        if (!game.getHostId().equals(host)) {
            throw new OnlyHostCanModerateException();
        }

        game.addToBlackList(user);
        game.remove(user);

        return game;
    }
}
