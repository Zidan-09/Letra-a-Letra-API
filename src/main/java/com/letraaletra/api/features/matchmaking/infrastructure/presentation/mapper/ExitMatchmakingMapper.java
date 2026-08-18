package com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.matchmaking.application.input.ExitMatchmakingQueueInput;

import java.util.UUID;

public class ExitMatchmakingMapper {
    public static ExitMatchmakingQueueInput toInput(UUID userId) {
        return new ExitMatchmakingQueueInput(
                userId
        );
    }
}
