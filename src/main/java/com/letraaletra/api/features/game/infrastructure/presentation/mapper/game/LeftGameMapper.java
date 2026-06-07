package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.LeftGameResponse;

public class LeftGameMapper {
    public static LeftGameInput toInput(LeftGameWsRequest request, String session) {
        return new LeftGameInput(
                request.tokenGameId(),
                session
        );
    }

    public static LeftGameResponse toResponse(LeftGameOutput output) {
        return new LeftGameResponse(
                GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
