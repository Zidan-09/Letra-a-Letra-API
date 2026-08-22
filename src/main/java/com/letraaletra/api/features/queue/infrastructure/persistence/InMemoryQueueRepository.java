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
    private final Map<QueueType, Queue<OnlineUser>> queues = new EnumMap<>(QueueType.class);

    public InMemoryQueueRepository() {
        for (QueueType type : QueueType.values()) {
            queues.put(type, new ConcurrentLinkedDeque<>());
        }
    }

    @Override
    public void add(QueueType type, OnlineUser onlineUser) {
       if (users.putIfAbsent(onlineUser.userId(), onlineUser) == null) {
           Queue<OnlineUser> queue = queues.get(type);

           queue.add(onlineUser);
       }
    }

    @Override
    public void remove(UUID id) {
        OnlineUser onlineUser = users.remove(id);
        
        if (onlineUser == null) return;
        
        queues.values().forEach(q -> q.remove(onlineUser));
    }

    @Override
    public boolean onQueue(UUID userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<QueueMatch> pollPair(QueueType type) {
        Queue<OnlineUser> queue = queues.get(type);

        synchronized (queue) {
            if (queue.size() < 2) {
                return Optional.empty();
            }

            OnlineUser first = queue.poll();
            OnlineUser second = queue.poll();

            if (first == null || second == null) {
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
