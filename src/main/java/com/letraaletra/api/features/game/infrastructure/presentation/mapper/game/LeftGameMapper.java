package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.LeftGameResponse;

import java.util.UUID;

public class LeftGameMapper {
    public static LeftGameInput toInput(LeftGameWsRequest request, String session, UUID userId) {
        return new LeftGameInput(
                UUID.fromString(request.gameId()),
                userId,
                session
        );
    }

    public static LeftGameResponse toResponse(LeftGameOutput output) {
        return new LeftGameResponse(
                GameResponseMapper.toResponse(output.game())
        );
    }
}
