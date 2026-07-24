package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanModerateException;

import java.util.UUID;

public class UnbanParticipantActorCommand implements ActorCommand<Game> {
    private final UUID user;
    private final UUID host;

    public UnbanParticipantActorCommand(UUID user, UUID host) {
        this.user = user;
        this.host = host;
    }

    @Override
    public Game execute(Game game) {
        if (!game.getHostId().equals(host)) {
            throw new OnlyHostCanModerateException();
        }

        game.getParticipants().removeFromBlackList(user);

        return game;
    }
}
