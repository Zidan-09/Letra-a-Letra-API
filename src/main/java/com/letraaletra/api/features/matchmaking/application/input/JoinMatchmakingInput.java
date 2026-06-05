package com.letraaletra.api.features.matchmaking.application.input;

import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.matchmaking.MatchmakingUser;

public record JoinMatchmakingInput(
    MatchmakingUser matchmakingUser,
    GameMode gameMode
) {
}
