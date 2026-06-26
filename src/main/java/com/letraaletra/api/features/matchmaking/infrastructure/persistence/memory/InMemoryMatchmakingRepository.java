package com.letraaletra.api.features.matchmaking.infrastructure.persistence.memory;

import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.domain.MatchUserData;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.matchmaking.domain.QueuedUser;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryMatchmakingRepository implements MatchmakingRepository {
    private final Map<GameMode, Queue<MatchUserData>> queues = new ConcurrentHashMap<>();
    private final Map<UUID, QueuedUser> queuedUser = new ConcurrentHashMap<>();
    private final Map<UUID, MatchUserData> users = new ConcurrentHashMap<>();

    public InMemoryMatchmakingRepository() {
        for (GameMode mode : GameMode.values()) {
            queues.put(mode, new ConcurrentLinkedDeque<>());
        }
    }

    @Override
    public void add(MatchUserData matchUserData, GameMode gameMode) {
        if (users.putIfAbsent(matchUserData.userId(), matchUserData) == null) {
            queues.get(gameMode).add(matchUserData);
            queuedUser.put(matchUserData.userId(), new QueuedUser(matchUserData, gameMode));
        }
    }

    @Override
    public void remove(UUID id) {
        MatchUserData user = users.remove(id);

        if (user == null) return;

        QueuedUser queued = queuedUser.remove(id);

        queues.get(queued.gameMode()).remove(user);
    }

    @Override
    public boolean onQueue(UUID userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<MatchmakingPair> pollPair(GameMode gameMode) {
        Queue<MatchUserData> queue = queues.get(gameMode);

        synchronized (queue) {
            if (queue.size() < 2) {
                return Optional.empty();
            }

            MatchUserData first = queue.poll();
            MatchUserData second = queue.poll();

            if (first == null || second == null) return Optional.empty();

            users.remove(first.userId());
            users.remove(second.userId());

            return Optional.of(new MatchmakingPair(first, second));
        }
    }
}
