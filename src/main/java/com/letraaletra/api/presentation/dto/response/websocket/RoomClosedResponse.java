package com.letraaletra.api.presentation.dto.response.websocket;

import com.letraaletra.api.domain.game.RoomCloseReasons;

public record RoomClosedResponse(
        RoomCloseReasons reason
) implements WsResponseDTO {
}
