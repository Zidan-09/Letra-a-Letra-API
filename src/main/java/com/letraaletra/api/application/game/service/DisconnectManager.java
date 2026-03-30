package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameStateResponseAssembler;
import com.letraaletra.api.presentation.dto.response.websocket.GameStateUpdatedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class DisconnectManager {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameStateResponseAssembler gameStateResponseAssembler;

    @Autowired
    private BroadcastService broadcast;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    public void start(String userId, String gameId) {
        String key = buildKey(userId, gameId);

        cancel(userId, gameId);

        ScheduledFuture<?> future = scheduler.schedule(() -> handleTimeout(userId, gameId), 45, TimeUnit.SECONDS);

        timers.put(key, future);
    }

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

        GameStateUpdatedWsResponse response = buildResponse(game.getGameState(), mapUsersFromGame(game));

        broadcast.send(gameId, response);
    }

    private String buildKey(String userId, String gameId) {
        return userId + ":" + gameId;
    }

    private Map<String, User> mapUsersFromGame(Game game) {
        List<String> ids = game.getGameState().getPlayerIds();
        return userRepository.findMapByIds(ids);
    }

    private GameStateUpdatedWsResponse buildResponse(GameState state, Map<String, User> userMap) {
        return new GameStateUpdatedWsResponse(
            gameStateResponseAssembler.get(state, userMap)
        );
    }
}