package com.letraaletra.api.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

import java.util.List;

public class JpaGameRepository implements GameRepository {
    @Override
    public void save(Game game) {

    }

    @Override
    public Game find(String id) {
        return null;
    }

    @Override
    public List<Game> get() {
        return List.of();
    }
}
