package com.letraaletra.api.features.game.domain.repository;

import com.letraaletra.api.features.game.domain.Game;

public interface GameRepository {
    void save(Game game);
}
