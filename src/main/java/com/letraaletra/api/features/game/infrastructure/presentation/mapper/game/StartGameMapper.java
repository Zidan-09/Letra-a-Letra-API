package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.domain.state.GameSettings;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.StartGameResponse;
import org.springframework.stereotype.Component;

@Component
public class StartGameMapper {
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
                GameStateDTOMapper.toGlobalDto(output.game())
        );
    }
}
