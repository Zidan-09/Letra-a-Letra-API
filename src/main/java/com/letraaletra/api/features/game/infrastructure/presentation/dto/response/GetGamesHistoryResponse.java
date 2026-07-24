package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

public record GetGamesHistoryResponse(
        PageResponse<GameHistory> games
) {
}
