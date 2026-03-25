package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.Game;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        this.games.put(game.getId(), game);
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
