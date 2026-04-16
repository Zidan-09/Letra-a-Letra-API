package com.letraaletra.api.infrastructure.persistence.memory;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        games.put(game.getId(), game);
    }

    @Override
    public Game find(String id) {
        return games.get(id);
    }

    @Override
    public List<Game> get() {
        return List.copyOf(games.values());
    }
}
