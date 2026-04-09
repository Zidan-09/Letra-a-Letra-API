package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;

public interface MatchmakingRepository {
    void add(MatchmakingUser user);
    void remove(MatchmakingUser user);
    MatchmakingUser poll();
    boolean isEmpty();
}
