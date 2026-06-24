package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.player.domain.Player;

import java.util.UUID;

public class DiscardPowerActorCommand implements ActorCommand<Game> {
    private final UUID userId;
    private final String powerId;

    public DiscardPowerActorCommand(UUID userId, String powerId) {
        this.userId = userId;
        this.powerId = powerId;
    }

    @Override
    public Game execute(Game game) {
        Player player = game.getGameState().getPlayerOrThrow(userId);

        player.removeFromInventoryOrThrow(powerId);

        return game;
    }
}
