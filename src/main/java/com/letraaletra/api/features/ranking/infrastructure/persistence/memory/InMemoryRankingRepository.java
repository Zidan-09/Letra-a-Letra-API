package com.letraaletra.api.features.ranking.infrastructure.persistence.memory;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.shared.domain.OnlineUser;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryRankingRepository implements RankingRepository {
    private final Map<UUID, OnlineUser> users = new ConcurrentHashMap<>();
    private final Queue<OnlineUser> queue = new ConcurrentLinkedDeque<>();

    @Override
    public void add(OnlineUser onlineUser) {
       if (users.putIfAbsent(onlineUser.userId(), onlineUser) == null) {
           queue.add(onlineUser);
       }
    }

    @Override
    public void remove(UUID id) {
        OnlineUser onlineUser = users.remove(id);

        if (onlineUser == null) return;

        queue.remove(onlineUser);
    }

    @Override
    public boolean onQueue(UUID userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<MatchmakingPair> pollPair() {
        synchronized (queue) {
            if (queue.size() < 2) {
                return Optional.empty();
            }

            OnlineUser first = queue.poll();
            OnlineUser second = queue.poll();

            if (first == null || second == null) return Optional.empty();

            users.remove(first.userId());
            users.remove(second.userId());

            return Optional.of(new MatchmakingPair(first, second));
        }
    }
}
