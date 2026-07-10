package com.letraaletra.api.features.ranking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ranking.application.input.ExitRankingQueueInput;

import java.util.UUID;

public class ExitRankingMapper {
    public static ExitRankingQueueInput toInput(UUID userId) {
        return new ExitRankingQueueInput(
                userId
        );
    }
}
