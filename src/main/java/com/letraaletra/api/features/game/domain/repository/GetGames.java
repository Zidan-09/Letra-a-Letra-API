package com.letraaletra.api.features.game.domain.repository;

import com.letraaletra.api.features.game.application.input.GetGamesInput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameHistory;
import org.springframework.data.domain.Page;

public interface GetGames {
    Page<GameHistory> get(GetGamesInput input);
}
