package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.player.domain.Player;

public class DiscardPowerActorCommand implements ActorCommand<Game> {
    private final String userId;
    private final String powerId;

    public DiscardPowerActorCommand(String userId, String powerId) {
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
