package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.Game;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<String, Game> games;

    public InMemoryGameRepository() {
        this.games = new HashMap<>();
    }

    @Override
    public Game find(String id) {
        return games.get(id);
    }

    @Override
    public void save(Game game) {
        this.games.replace(game.getId(), game);
    }

    @Override
    public List<Game> get() {
        return List.copyOf(games.values().stream().toList());
    }
}
