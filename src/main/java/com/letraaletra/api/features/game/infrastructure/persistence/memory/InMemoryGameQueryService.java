package com.letraaletra.api.features.game.infrastructure.persistence.memory;

import com.letraaletra.api.shared.infrastructure.concurrency.Actor;
import com.letraaletra.api.shared.infrastructure.concurrency.ActorManager;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class InMemoryGameQueryService implements GameQueryService {
    private final ActorManager<Game> actorManager;

    public InMemoryGameQueryService(ActorManager<Game> actorManager) {
        this.actorManager = actorManager;
    }

    @Override
    public boolean existsByCode(String code) {
        return getGames()
                .anyMatch(game -> game.getCode().equals(code));
    }

    @Override
    public Game findByCode(String code) {
        return getGames()
                .filter(game -> game.getCode().equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public List<Game> getPublic() {
        return getGames()
                .filter(game -> !game.getRoomSettings().isPrivateGame())
                .toList();
    }

    @Override
    public List<Game> getAllActiveGames() {
        return List.copyOf(getGames().toList());
    }

    private Stream<Game> getGames() {
        return actorManager.getAllActors().stream()
                .map(Actor::getGame);
    }
}
