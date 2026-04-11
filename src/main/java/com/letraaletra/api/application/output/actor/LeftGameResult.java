package com.letraaletra.api.application.output.actor;

import com.letraaletra.api.domain.game.Game;

public record LeftGameResult(
        Game game,
        String user,
        boolean isEmpty
) {}