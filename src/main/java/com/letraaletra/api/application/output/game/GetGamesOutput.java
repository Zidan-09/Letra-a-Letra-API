package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.Game;

import java.util.List;

public record GetGamesOutput(
        List<Game> games
) {
}
