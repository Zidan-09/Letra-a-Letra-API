package com.letraaletra.api.features.game.domain.repository;

import com.letraaletra.api.features.game.domain.Game;

public interface GameRepository {
    Game save(Game game);
}
