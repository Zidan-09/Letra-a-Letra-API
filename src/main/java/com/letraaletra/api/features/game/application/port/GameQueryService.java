package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.Game;

import java.util.List;

public interface GameQueryService {
    Game findByCode(String code);
    boolean existsByCode(String code);
    List<Game> getAllActiveGames();
    List<Game> getPublic();
}
