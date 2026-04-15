package com.letraaletra.api.infrastructure.manager;

import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorRepository;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infrastructure.actor.GameActor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class GameActorRepository implements ActorRepository {
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();
    private final GameRepository gameRepository;
    private final ExecutorService executor;

    public GameActorRepository(GameRepository gameRepository, ExecutorService executor) {
        this.gameRepository = gameRepository;
        this.executor = executor;
    }

    @Override
    public Actor getOrCreate(String id) {
        Actor existingActor = actors.get(id);
        if (existingActor != null) {
            return existingActor;
        }

        Game game = gameRepository.find(id);
        if (game == null) {
            throw new GameNotFoundException();
        }

        Actor newActor = new GameActor(executor, game, gameRepository);
        Actor concurrentActor = actors.putIfAbsent(id, newActor);

        return (concurrentActor != null) ? concurrentActor : newActor;
    }

    @Override
    public void remove(String id) {
        actors.remove(id);
    }
}
