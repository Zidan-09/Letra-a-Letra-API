package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.CreateGameResponse;

import java.util.UUID;

public class CreateGameMapper {
    public static CreateGameInput toInput(CreateGameWsRequest request, String sessionId, String userId) {
        return new CreateGameInput(
                request.name(),
                new RoomSettings(
                        request.settings().allowSpectators(),
                        request.settings().privateGame()
                ),
                sessionId,
                UUID.fromString(userId)
        );
    }

    public static CreateGameResponse toResponseDTO(CreateGameOutput output) {
        return new CreateGameResponse(
               GameDTOMapper.toDTO(output.game())
        );
    }
}
