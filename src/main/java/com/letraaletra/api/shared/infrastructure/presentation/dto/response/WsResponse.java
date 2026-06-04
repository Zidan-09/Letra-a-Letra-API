package com.letraaletra.api.shared.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "event"
)

public interface WsResponse {}
