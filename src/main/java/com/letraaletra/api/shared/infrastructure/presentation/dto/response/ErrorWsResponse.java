package com.letraaletra.api.shared.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ERROR")
public record ErrorWsResponse(
        String message
) implements WsResponse {}
