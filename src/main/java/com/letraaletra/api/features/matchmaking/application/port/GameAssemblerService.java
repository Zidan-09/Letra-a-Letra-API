package com.letraaletra.api.features.matchmaking.application.port;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.queue.domain.QueueType;

public interface GameAssemblerService {
    Game create(MatchmakingPair pair, QueueType type);
}
