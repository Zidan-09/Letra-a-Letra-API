package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.DiscardPowerResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateResponseMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DiscardPowerResponseMapper {
    public DiscardPowerInput toInput(DiscardPowerWsRequest request, String userId) {
        return new DiscardPowerInput(
                UUID.fromString(request.gameId()),
                UUID.fromString(userId),
                request.powerId()
        );
    }

    public DiscardPowerResponse toResponse(DiscardPowerOutput output, UUID viewer) {
        return new DiscardPowerResponse(
                GameStateResponseMapper.toResponse(output.game(), viewer)
        );
    }
}
