package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

import java.time.Instant;

@JsonTypeName("RANKING_GAME")
public record RankSuccessResponse(
        MatchmakingStatus status,
        Instant turnEndsAt,
        String gameId,
        GameStateResponse data
) implements WsResponse {
}
