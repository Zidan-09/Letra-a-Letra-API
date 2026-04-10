package com.letraaletra.api.infrastructure.persistence.repository;

import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryMatchmakingRepository implements MatchmakingRepository {
    private final Map<GameMode, Queue<MatchmakingUser>> queues = new ConcurrentHashMap<>();
    private final Map<MatchmakingUser, GameMode> queueLocal = new ConcurrentHashMap<>();
    private final Map<String, MatchmakingUser> matchmakingUserMap = new ConcurrentHashMap<>();
    private final Set<String> users = ConcurrentHashMap.newKeySet();

    public InMemoryMatchmakingRepository() {
        for (GameMode mode : GameMode.values()) {
            queues.put(mode, new ConcurrentLinkedDeque<>());
        }
    }

    @Override
    public void add(MatchmakingUser matchmakingUser, GameMode gameMode) {
        if (users.add(matchmakingUser.user())) {
            queues.get(gameMode).add(matchmakingUser);
            queueLocal.put(matchmakingUser, gameMode);
            matchmakingUserMap.put(matchmakingUser.user(), matchmakingUser);
        }
    }

    @Override
    public void remove(MatchmakingUser matchmakingUser) {
        if (users.remove(matchmakingUser.user())) {
            queues.get(queueLocal.get(matchmakingUser)).remove(matchmakingUser);
            queueLocal.remove(matchmakingUser);
            matchmakingUserMap.remove(matchmakingUser.user());
        }
    }

    @Override
    public void removeById(String id) {
        if (users.remove(id)) {
            MatchmakingUser matchmakingUser = matchmakingUserMap.get(id);
            queues.get(queueLocal.get(matchmakingUser)).remove(matchmakingUser);
            queueLocal.remove(matchmakingUser);
            matchmakingUserMap.remove(matchmakingUser.user());
        }
    }

    @Override
    public MatchmakingUser poll(GameMode gameMode) {
        MatchmakingUser matchmakingUser = queues.get(gameMode).poll();
        queueLocal.remove(matchmakingUser);

        if (matchmakingUser != null) {
            users.remove(matchmakingUser.user());
        }

        return matchmakingUser;
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public boolean onQueue(String userId) {
        return users.contains(userId);
    }
}
