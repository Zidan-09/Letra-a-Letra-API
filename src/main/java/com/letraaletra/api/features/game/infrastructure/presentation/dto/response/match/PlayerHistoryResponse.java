package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match;

public record PlayerHistoryResponse(
        String id,
        String nickname,
        int score,
        boolean winner
) {}
