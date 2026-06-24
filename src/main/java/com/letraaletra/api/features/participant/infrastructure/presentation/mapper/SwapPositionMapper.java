package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.SwapPositionResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameDTOMapper;

import java.util.UUID;

public class SwapPositionMapper {
    public static SwapPositionInput toInput(SwapPositionWsRequest request, String userId) {
        return new SwapPositionInput(
                request.tokenGameId(),
                request.position(),
                UUID.fromString(userId)
        );
    }

    public static SwapPositionResponse toResponse(SwapPositionOutput output) {
        return new SwapPositionResponse(
                GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
