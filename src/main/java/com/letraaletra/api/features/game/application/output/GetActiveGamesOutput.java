package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;
import org.springframework.data.domain.Page;

public record GetActiveGamesOutput(
        Page<Game> games
) {
}
