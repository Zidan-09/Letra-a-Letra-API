package com.letraaletra.api.application.port;

import com.letraaletra.api.application.command.actor.ActorCommand;

import java.util.concurrent.CompletableFuture;

public interface Actor {
     <T> CompletableFuture<T> enqueueCommand(ActorCommand<T> command);
}
