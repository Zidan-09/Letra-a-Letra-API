package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.domain.state.GameSettings;
import com.letraaletra.api.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.StartGameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartGameMapper {

    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public StartGameInput toCommand(StartGameWsRequest request, String session) {
        return new StartGameInput(
                request.tokenGameId(),
                session,
                new GameSettings(
                        request.settings().themeId(),
                        request.settings().gameMode()
                )
        );
    }

    public StartGameResponse toResponseDTO(StartGameOutput output) {
        return new StartGameResponse(
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
