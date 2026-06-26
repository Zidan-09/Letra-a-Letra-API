package com.letraaletra.api.features.matchmaking.application.input;

import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.domain.MatchUserData;

public record JoinMatchmakingInput(
    MatchUserData matchUserData,
    GameMode gameMode
) {
}
