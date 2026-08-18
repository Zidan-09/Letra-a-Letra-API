package com.letraaletra.api.features.game.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("JOIN_GAME")
public record JoinGameWsRequest(
        @NotBlank
        String gameId
) implements WsRequest {
        @Override
        public String getAudit() {
                return "joined in game";
        }
}
