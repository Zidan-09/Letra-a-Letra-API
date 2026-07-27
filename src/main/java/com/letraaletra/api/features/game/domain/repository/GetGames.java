package com.letraaletra.api.features.game.domain.repository;

import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.domain.GamesPage;
import org.springframework.data.domain.Page;

public interface GetGames {
    Page<GameHistory> get(GamesPage page);
}
