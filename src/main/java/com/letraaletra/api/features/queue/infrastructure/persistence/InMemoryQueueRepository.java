package com.letraaletra.api.features.queue.infrastructure.persistence;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.queue.domain.OnlineUser;
import com.letraaletra.api.features.queue.domain.QueueMatch;
import com.letraaletra.api.features.queue.domain.QueueType;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryQueueRepository implements QueueRepository {
    private final Map<UUID, OnlineUser> users = new ConcurrentHashMap<>();
    private final Map<QueueType, Deque<OnlineUser>> queues = new EnumMap<>(QueueType.class);
    private final Object lock = new Object();

    public InMemoryQueueRepository() {
        for (QueueType type : QueueType.values()) {
            queues.put(type, new ConcurrentLinkedDeque<>());
        }
    }

    @Override
    public void add(QueueType type, OnlineUser onlineUser) {
        synchronized (lock) {
            if (users.putIfAbsent(onlineUser.userId(), onlineUser) == null) {
                queues.get(type).add(onlineUser);
            }
        }
    }

    @Override
    public void remove(UUID id) {
        synchronized (lock) {
            OnlineUser onlineUser = users.remove(id);

            if (onlineUser == null) return;

            queues.values().forEach(q -> q.remove(onlineUser));
        }
    }

    @Override
    public boolean onQueue(UUID userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<QueueMatch> pollPair(QueueType type) {
        Deque<OnlineUser> queue = queues.get(type);

        synchronized (lock) {
            if (queue.size() < 2) {
                return Optional.empty();
            }

            OnlineUser first = queue.poll();
            OnlineUser second = queue.poll();

            if (first == null || second == null) {
                if (first != null) {
                    queue.offerFirst(first);
                }
                return Optional.empty();
            }

            users.remove(first.userId());
            users.remove(second.userId());

            return Optional.of(
                    new QueueMatch(
                            new MatchmakingPair(first, second),
                            type
                    )
            );
        }
    }
}
