package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match;

import java.time.Instant;
import java.util.List;

public record MatchHistoryResponse(
        Instant finishedAt,
        List<PlayerHistoryResponse> players
) {}