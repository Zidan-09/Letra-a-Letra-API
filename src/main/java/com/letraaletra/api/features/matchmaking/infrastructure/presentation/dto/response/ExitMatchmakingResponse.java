package com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;

@JsonTypeName("EXIT_MATCHMAKING")
public record ExitMatchmakingResponse(
        MatchmakingMessages message
) {
}
