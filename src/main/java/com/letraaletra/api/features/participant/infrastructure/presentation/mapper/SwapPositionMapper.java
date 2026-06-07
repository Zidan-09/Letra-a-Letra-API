package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.SwapPositionResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameDTOMapper;
import org.springframework.stereotype.Component;

@Component
public class SwapPositionMapper {
    public SwapPositionInput toCommand(SwapPositionWsRequest request, String userId) {
        return new SwapPositionInput(
                request.tokenGameId(),
                request.position(),
                userId
        );
    }

    public SwapPositionResponse toResponseDTO(SwapPositionOutput output) {
        return new SwapPositionResponse(
                GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
