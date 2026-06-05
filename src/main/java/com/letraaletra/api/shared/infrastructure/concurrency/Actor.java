package com.letraaletra.api.shared.infrastructure.concurrency;

import com.letraaletra.api.application.command.actor.ActorCommand;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public interface Actor {
     <T> CompletableFuture<T> enqueueCommand(ActorCommand<T> command);
     Game getGame();
}
