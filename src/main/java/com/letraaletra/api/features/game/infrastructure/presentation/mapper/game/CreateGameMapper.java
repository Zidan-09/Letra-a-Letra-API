package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.CreateGameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateGameMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public CreateGameInput toCommand(CreateGameWsRequest request, String sessionId, String userId) {
        return new CreateGameInput(
                request.name(),
                new RoomSettings(
                        request.settings().allowSpectators(),
                        request.settings().privateGame()
                ),
                sessionId,
                userId
        );
    }

    public CreateGameResponse toResponseDTO(CreateGameOutput output) {
        return new CreateGameResponse(
               gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
