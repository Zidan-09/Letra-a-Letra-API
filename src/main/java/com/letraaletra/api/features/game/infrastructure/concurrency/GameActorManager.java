package com.letraaletra.api.features.game.infrastructure.concurrency;

import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class GameActorManager implements ActorManager<Game> {
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public GameActorManager(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void create(String id, Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        Actor newActor = new GameActor(executor, game);
        actors.putIfAbsent(id, newActor);
    }

    @Override
    public Actor get(String id) {
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
    public void remove(String id) {
        actors.remove(id);
    }
}
