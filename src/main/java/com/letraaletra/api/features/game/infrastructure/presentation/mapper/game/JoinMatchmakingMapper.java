package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.domain.MatchUserData;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.JoinMatchmakingResponse;

import java.util.UUID;

public class JoinMatchmakingMapper {
    public static JoinMatchmakingInput toInput(UUID user, String session, GameMode gameMode) {
        MatchUserData matchUserData = new MatchUserData(user, session);

        return new JoinMatchmakingInput(matchUserData, gameMode);
    }

    public static JoinMatchmakingResponse toResponse() {
        return new JoinMatchmakingResponse(
                MatchmakingStatus.SEARCHING
        );
    }
}
