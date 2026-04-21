package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.game.Game;

public interface GameRepository {
    Game save(Game game);
}
