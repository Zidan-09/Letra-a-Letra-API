package com.letraaletra.api.shared.application.port;

import com.letraaletra.api.features.game.domain.actor.command.ActorCommand;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public interface Actor {
     <T> CompletableFuture<T> enqueueCommand(ActorCommand<T> command);
     Game getGame();
}
