package com.letraaletra.api.infrastructure.actor;

import com.letraaletra.api.application.command.actor.ActorCommand;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameActor implements Actor {
    private final Queue<CommandEnvelope<?>> mailbox = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor;
    private final GameRepository gameRepository;
    private final Game game;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    private record CommandEnvelope<T>(
            ActorCommand<T> command,
            CompletableFuture<T> future
    ) {}

    public GameActor(ExecutorService executor, Game game, GameRepository gameRepository) {
        this.game = game;
        this.executor = executor;
        this.gameRepository = gameRepository;
    }

    @Override
    public <T> CompletableFuture<T> enqueueCommand(ActorCommand<T> command) {
        CompletableFuture<T> future = new CompletableFuture<>();
        mailbox.add(new CommandEnvelope<>(command, future));
        trySchedule();
        return future;
    }

    private void trySchedule() {
        if (processing.compareAndSet(false, true)) {
            executor.submit(this::processQueue);
        }
    }

    private void processQueue() {
        try {
            CommandEnvelope<?> envelope;
            while ((envelope = mailbox.poll()) != null) {
                executeEnvelope(envelope);
            }
        } finally {
            processing.set(false);

            if (!mailbox.isEmpty()) {
                trySchedule();
            }
        }
    }

    private <T> void executeEnvelope(CommandEnvelope<T> envelope) {
        try {
            T result = envelope.command().execute(game);

            gameRepository.save(game);

            envelope.future().complete(result);

        } catch (Throwable t) {
            envelope.future().completeExceptionally(t);
        }
    }
}
