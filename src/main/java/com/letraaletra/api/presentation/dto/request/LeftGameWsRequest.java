package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("LEFT_GAME")
public record LeftGameWsRequest(
        @NotBlank
        String tokenGameId
) implements WsRequest {}
