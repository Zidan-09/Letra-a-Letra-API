package com.letraaletra.api.infrastructure.persistence.repository;

import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import org.springframework.stereotype.Repository;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryMatchmakingRepository implements MatchmakingRepository {
    private final Queue<MatchmakingUser> queue = new ConcurrentLinkedDeque<>();
    private final Set<String> users = ConcurrentHashMap.newKeySet();

    @Override
    public void add(MatchmakingUser matchmakingUser) {
        if (users.add(matchmakingUser.user())) {
            queue.add(matchmakingUser);
        }
    }

    @Override
    public void remove(MatchmakingUser matchmakingUser) {
        if (users.remove(matchmakingUser.user())) {
            queue.remove(matchmakingUser);
        }
    }

    @Override
    public MatchmakingUser poll() {
        MatchmakingUser matchmakingUser = queue.poll();

        if (matchmakingUser != null) {
            users.remove(matchmakingUser.user());
        }

        return matchmakingUser;
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }
}
