package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public class KickParticipantActorCommand implements ActorCommand<Game> {
    private final User target;
    private final UUID hostId;

    public KickParticipantActorCommand(User target, UUID hostId) {
        this.target = target;
        this.hostId = hostId;
    }

    @Override
    public Game execute(Game game) {
        game.kickParticipant(hostId, target.getUserId());

        target.leaveGame();

        return game;
    }
}
