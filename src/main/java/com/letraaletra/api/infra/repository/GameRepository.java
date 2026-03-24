package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.dto.response.game.GameDTO;

import java.util.List;

public interface GameRepository {
    void save(Game game);
    Game find(String id);
    List<GameDTO> get();
}
