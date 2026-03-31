package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.RoomCloseReasons;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.response.websocket.RoomClosedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TimeoutManager {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BroadcastService broadcast;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    public void start(String gameId) {
        cancel(gameId);

        ScheduledFuture<?> future = scheduler.schedule(() -> handleTimeout(gameId), 5, TimeUnit.MINUTES);

        timers.put(gameId, future);
    }

    public void cancel(String gameId) {
        ScheduledFuture<?> future = timers.remove(gameId);

        if (future != null) {
            future.cancel(false);
        }
    }

    private void handleTimeout(String gameId) {
        timers.remove(gameId);

        Game game = gameRepository.find(gameId);

        if (game == null) return;

        gameRepository.removeByCode(game.getCode());

        broadcast.send(gameId, new RoomClosedResponse(RoomCloseReasons.INACTIVITY));
    }
}
