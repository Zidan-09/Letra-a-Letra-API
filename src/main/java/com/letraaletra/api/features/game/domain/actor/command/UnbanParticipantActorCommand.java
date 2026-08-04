package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;

public class UnbanParticipantActorCommand implements ActorCommand<Game> {
    private final UUID targetId;
    private final UUID hostId;

    public UnbanParticipantActorCommand(UUID targetId, UUID hostId) {
        this.targetId = targetId;
        this.hostId = hostId;
    }

    @Override
    public Game execute(Game game) {
        game.removeBan(hostId, targetId);

        return game;
    }
}
