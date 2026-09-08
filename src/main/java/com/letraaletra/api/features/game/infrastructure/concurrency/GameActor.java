package com.letraaletra.api.features.game.infrastructure.concurrency;

import com.letraaletra.api.features.game.application.port.TransactionalExecutorService;
import com.letraaletra.api.features.game.domain.actor.command.ActorCommand;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.shared.application.port.OperationContext;
import com.letraaletra.api.features.game.domain.Game;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameActor implements Actor {
    private final Queue<CommandEnvelope<?>> mailbox = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor;
    private final TransactionalExecutorService transactionExecutor;
    private final OperationContext operationContext;
    private final Game game;
    private final com.letraaletra.api.features.game.domain.repository.SaveGame saveGame;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    private record CommandEnvelope<T>(
            ActorCommand<T> command,
            CompletableFuture<T> future
    ) {}

    public GameActor(
            ExecutorService executor,
            TransactionalExecutorService transactionExecutor,
            OperationContext operationContext,
            Game game
    ) {
        this(executor, transactionExecutor, operationContext, game, null);
    }

    public GameActor(
            ExecutorService executor,
            TransactionalExecutorService transactionExecutor,
            OperationContext operationContext,
            Game game,
            com.letraaletra.api.features.game.domain.repository.SaveGame saveGame
    ) {
        this.game = game;
        this.transactionExecutor = transactionExecutor;
        this.operationContext = operationContext;
        this.executor = executor;
        this.saveGame = saveGame;
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
        UUID operationId = UUID.randomUUID();

        try {
            operationContext.runAsOperation(operationId, game.getId().toString(), () -> {
                T result = transactionExecutor.execute(() -> {
                    T r = envelope.command().execute(game);
                    if (saveGame != null) {
                        saveGame.save(game);
                    }
                    return r;
                });

                envelope.future().complete(result);
            });

        } catch (Throwable t) {
            envelope.future().completeExceptionally(t);
        }
    }

    @Override
    public Game getGame() {
        return game;
    }
}
