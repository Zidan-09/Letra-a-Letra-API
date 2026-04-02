package com.letraaletra.api.infrastructure.websocket;

import com.letraaletra.api.application.usecase.participant.DisconnectScheduler;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class DisconnectManager implements DisconnectScheduler {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

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

        Game game = gameRepository.find(gameId);
        User user = userRepository.find(userId);

        if (game == null || user == null) return;

        Participant participant = game.getParticipantByUserId(userId);
        if (participant == null) return;

        if (participant.isConnected()) return;

        game.remove(userId);
        user.leaveGame();

        gameRepository.save(game);
        userRepository.save(user);
    }

    private String buildKey(String userId, String gameId) {
        return userId + ":" + gameId;
    }
}