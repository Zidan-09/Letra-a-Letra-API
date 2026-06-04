package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("MODERATION_MESSAGE")
public record ModerationResponse(
        String warning
) implements WsResponse {
}
