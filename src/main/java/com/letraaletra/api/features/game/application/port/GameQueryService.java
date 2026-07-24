package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.domain.Game;
import org.springframework.data.domain.Page;

public interface GameQueryService {
    Game findByCode(String code);
    boolean existsByCode(String code);
    Page<Game> getAllActiveGames(GetActiveGamesInput input);
    Page<Game> getPublic(GetPublicGamesInput input);
}
