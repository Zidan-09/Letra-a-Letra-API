package com.letraaletra.api.features.matchmaking.domain.repository;

import com.letraaletra.api.features.game.domain.matchmaking.MatchmakingUser;

import java.util.UUID;

public interface RemoveUser {
    void remove(MatchmakingUser user);
    void removeById(UUID id);
}
