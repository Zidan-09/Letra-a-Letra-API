package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("EXIT_RANKING")
public record ExitRankingResponse(
        MatchmakingMessages message
) implements WsResponse {
}
