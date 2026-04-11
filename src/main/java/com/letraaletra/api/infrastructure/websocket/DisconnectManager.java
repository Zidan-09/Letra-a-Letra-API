package com.letraaletra.api.infrastructure.websocket;

import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.application.service.LeaveGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class DisconnectManager implements DisconnectScheduler {

    @Autowired
    private LeaveGameService leaveGameService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    @Override
    public void start(String userId, String gameId) {
        String key = buildKey(userId, gameId);

        cancel(userId, gameId);

        ScheduledFuture<?> future = scheduler.schedule(() -> handleTimeout(userId, gameId), 45, TimeUnit.SECONDS);

        timers.put(key, future);
    }

    @Override
    public void cancel(String userId, String gameId) {
        String key = buildKey(userId, gameId);

        ScheduledFuture<?> future = timers.remove(key);

        if (future != null) {
            future.cancel(false);
        }
    }

    private void handleTimeout(String userId, String gameId) {
        String key = buildKey(userId, gameId);
        timers.remove(key);

        leaveGameService.leave(gameId, userId);
    }

    private String buildKey(String userId, String gameId) {
        return userId + ":" + gameId;
    }
}