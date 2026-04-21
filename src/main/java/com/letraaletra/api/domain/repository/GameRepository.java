package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.game.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> find(String id);
}
