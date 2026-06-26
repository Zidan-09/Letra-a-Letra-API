package com.letraaletra.api.features.matchmaking.domain;

import com.letraaletra.api.features.game.domain.state.GameMode;

public record QueuedUser(
        MatchUserData user,
        GameMode gameMode
) {
}
