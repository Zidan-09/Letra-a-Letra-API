package com.letraaletra.api.features.matchmaking.domain.repository;

import com.letraaletra.api.shared.domain.OnlineUser;
import com.letraaletra.api.features.game.domain.state.GameMode;

public interface EnqueueUser {
    void add(OnlineUser user, GameMode gameMode);
}
