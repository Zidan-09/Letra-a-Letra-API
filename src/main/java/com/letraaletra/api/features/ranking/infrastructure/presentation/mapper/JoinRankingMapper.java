package com.letraaletra.api.features.ranking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.JoinRankingResponse;
import com.letraaletra.api.shared.domain.OnlineUser;

import java.util.UUID;

public class JoinRankingMapper {
    public static JoinRankingInput toInput(UUID user, String session) {
        return new JoinRankingInput(
                new OnlineUser(user, session)
        );
    }

    public static JoinRankingResponse toResponse() {
        return new JoinRankingResponse(
                MatchmakingStatus.SEARCHING
        );
    }
}
