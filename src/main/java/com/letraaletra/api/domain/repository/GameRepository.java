package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.Game;

import java.util.List;

public interface GameRepository {
    void save(Game game);
    Game find(String id);
    List<Game> get();
}
