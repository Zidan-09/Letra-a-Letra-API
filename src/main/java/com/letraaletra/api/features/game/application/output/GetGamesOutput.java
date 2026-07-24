package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.GameHistory;
import org.springframework.data.domain.Page;

public record GetGamesOutput(
        Page<GameHistory> games
) {
}
