package com.letraaletra.api.features.matchmaking.domain.repository;

import com.letraaletra.api.features.matchmaking.domain.MatchUserData;
import com.letraaletra.api.features.game.domain.state.GameMode;

public interface EnqueueUser {
    void add(MatchUserData user, GameMode gameMode);
}
