package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.shared.domain.OnlineUser;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.JoinMatchmakingResponse;

import java.util.UUID;

public class JoinMatchmakingMapper {
    public static JoinMatchmakingInput toInput(UUID user, String session, GameMode gameMode) {
        OnlineUser onlineUser = new OnlineUser(user, session);

        return new JoinMatchmakingInput(onlineUser, gameMode);
    }

    public static JoinMatchmakingResponse toResponse() {
        return new JoinMatchmakingResponse(
                MatchmakingStatus.SEARCHING
        );
    }
}
