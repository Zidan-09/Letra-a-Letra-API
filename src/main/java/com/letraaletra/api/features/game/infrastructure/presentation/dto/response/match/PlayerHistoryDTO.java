package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match;

public record PlayerHistoryDTO(
        String id,
        String nickname,
        int score,
        boolean winner
) {}
