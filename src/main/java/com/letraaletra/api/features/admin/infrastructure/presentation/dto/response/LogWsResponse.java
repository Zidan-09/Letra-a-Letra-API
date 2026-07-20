package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("LOG")
public record LogWsResponse(
        String log
) implements WsAdminResponse {
}
