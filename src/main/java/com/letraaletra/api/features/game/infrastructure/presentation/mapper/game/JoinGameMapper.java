package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.JoinGameResponse;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.JoinGameWsRequest;

public class JoinGameMapper {
    public static JoinGameInput toInput(JoinGameWsRequest request, String sessionId, String userId) {
        return new JoinGameInput(
                request.tokenGameId(),
                sessionId,
                userId
        );
    }

    public static JoinGameResponse toResponse(JoinGameOutput output) {
        return new JoinGameResponse(
                GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
