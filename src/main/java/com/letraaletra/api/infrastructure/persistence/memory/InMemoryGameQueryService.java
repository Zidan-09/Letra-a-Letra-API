package com.letraaletra.api.infrastructure.persistence.memory;

import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.domain.game.Game;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMemoryGameQueryService implements GameQueryService {
    private final ActorManager actorManager;

    public InMemoryGameQueryService(ActorManager actorManager) {
        this.actorManager = actorManager;
    }

    @Override
    public boolean existsByCode(String code) {
        return actorManager.getAllActors().stream()
                .map(Actor::getGame)
                .anyMatch(game -> game.getCode().equals(code));
    }

    @Override
    public Game findByCode(String code) {
        return actorManager.getAllActors().stream()
                .map(Actor::getGame)
                .filter(game -> game.getCode().equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public List<Game> getPublic() {
        return actorManager.getAllActors().stream()
                .map(Actor::getGame)
                .filter(game -> !game.getRoomSettings().isPrivateGame())
                .toList();
    }

    @Override
    public List<Game> getAllActiveGames() {
        return List.copyOf(actorManager.getAllActors().stream()
                .map(Actor::getGame).toList()
        );
    }
}
