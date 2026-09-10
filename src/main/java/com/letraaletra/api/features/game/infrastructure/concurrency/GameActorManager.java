package com.letraaletra.api.features.game.infrastructure.concurrency;

import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.repository.SaveGame;
import com.letraaletra.api.shared.application.port.OperationContext;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class GameActorManager implements ActorManager<Game> {
    private final Map<UUID, Actor> actors = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private final TransactionalExecutorService transactionExecutor;
    private final OperationContext operationContext;
    private final SaveGame saveGame;

    @Autowired
    public GameActorManager(
            ExecutorService executor,
            TransactionalExecutorService transactionExecutor,
            OperationContext operationContext,
            @Autowired(required = false) SaveGame saveGame
    ) {
        this.executor = executor;
        this.transactionExecutor = transactionExecutor;
        this.operationContext = operationContext;
        this.saveGame = saveGame;
    }

    // Legacy constructor for tests without SaveGame
    public GameActorManager(
            ExecutorService executor,
            TransactionalExecutorService transactionExecutor,
            OperationContext operationContext
    ) {
        this(executor, transactionExecutor, operationContext, null);
    }

    @Override
    public void create(UUID id, Game actor) {
        if (actor == null) {
            throw new GameNotFoundException();
        }

        Actor newActor = new GameActor(executor, transactionExecutor, operationContext, actor, saveGame);
        actors.putIfAbsent(id, newActor);
    }

    @Override
    public Actor get(UUID id) {
        Actor existingActor = actors.get(id);

        if (existingActor == null) {
            throw new GameNotFoundException();
        }

        return existingActor;
    }

    @Override
    public List<Actor> getAllActors() {
        return List.copyOf(actors.values());
    }

    @Override
    public void remove(UUID id) {
        actors.remove(id);
    }

    @Override
    public long count() {
        return actors.size();
    }
}
