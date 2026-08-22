package com.letraaletra.api.features.matchmaking.application.port;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.queue.domain.QueueType;

public interface MatchmakingSenderService {
    void notify(Game game, QueueType type);
}
