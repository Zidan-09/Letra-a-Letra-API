package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.DiscardPowerResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameStateDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscardPowerDTOMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public DiscardPowerInput toCommand(DiscardPowerWsRequest request, String userId) {
        return new DiscardPowerInput(
                request.tokenGameId(),
                userId,
                request.powerId()
        );
    }

    public DiscardPowerResponseDTO toResponseDTO(DiscardPowerOutput output, String viewer) {
        return new DiscardPowerResponseDTO(
                gameStateDTOMapper.toDTO(output.game(), viewer)
        );
    }
}
