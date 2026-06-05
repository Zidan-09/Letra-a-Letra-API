package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.LeftGameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeftGameMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public LeftGameInput toCommand(LeftGameWsRequest request, String session) {
        return new LeftGameInput(
                request.tokenGameId(),
                session
        );
    }

    public LeftGameResponse toResponseDTO(LeftGameOutput output) {
        return new LeftGameResponse(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
