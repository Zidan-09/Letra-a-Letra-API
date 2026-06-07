package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.DiscardPowerResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;
import org.springframework.stereotype.Component;

@Component
public class DiscardPowerDTOMapper {
    public DiscardPowerInput toInput(DiscardPowerWsRequest request, String userId) {
        return new DiscardPowerInput(
                request.tokenGameId(),
                userId,
                request.powerId()
        );
    }

    public DiscardPowerResponse toResponseDTO(DiscardPowerOutput output, String viewer) {
        return new DiscardPowerResponse(
                GameStateDTOMapper.toDto(output.game(), viewer)
        );
    }
}
