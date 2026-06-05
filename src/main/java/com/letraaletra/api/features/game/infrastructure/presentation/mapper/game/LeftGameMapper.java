package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.LeftGameResponse;
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
