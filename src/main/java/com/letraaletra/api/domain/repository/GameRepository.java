package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.game.Game;

import java.util.List;

public interface GameRepository {
    void save(Game game);
    Game find(String id);
    Game findByCode(String code);
    boolean existsByCode(String code);
    List<Game> get();
    List<Game> getPublic();
    void removeByCode(String code);
}
