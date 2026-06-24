package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.game.domain.Game;

import java.util.List;
import java.util.Map;

public record GetGamesOutput(
        List<Game> games
) {
}
