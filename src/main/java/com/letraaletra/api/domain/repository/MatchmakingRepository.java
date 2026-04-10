package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;

public interface MatchmakingRepository {
    void add(MatchmakingUser user, GameMode gameMode);
    void remove(MatchmakingUser user);
    void removeById(String id);
    MatchmakingUser poll(GameMode gameMode);
    boolean isEmpty();
    boolean onQueue(String userId);
}
