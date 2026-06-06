package com.letraaletra.api.features.matchmaking.domain.repository;

import com.letraaletra.api.features.game.domain.matchmaking.MatchmakingUser;
import com.letraaletra.api.features.game.domain.state.GameMode;

public interface EnqueueUser {
    void add(MatchmakingUser user, GameMode gameMode);
}
