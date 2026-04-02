package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

@JsonTypeName("PLAYER_ACTION_RESULT")
public record PlayerActionResponseDTO(
        GameStateDTO data
) implements WsResponseDTO {
}
