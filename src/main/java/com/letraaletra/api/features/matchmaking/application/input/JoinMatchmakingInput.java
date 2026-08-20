package com.letraaletra.api.features.matchmaking.application.input;

import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.queue.domain.OnlineUser;

public record JoinMatchmakingInput(
    OnlineUser onlineUser,
    GameMode gameMode
) {
}
