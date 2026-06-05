package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.presentation.dto.response.websocket.JoinGameResponse;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.presentation.dto.request.JoinGameWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinGameMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public JoinGameInput toCommand(JoinGameWsRequest request, String sessionId, String userId) {
        return new JoinGameInput(
                request.tokenGameId(),
                sessionId,
                userId
        );
    }

    public JoinGameResponse toResponseDTO(JoinGameOutput output) {
        return new JoinGameResponse(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
