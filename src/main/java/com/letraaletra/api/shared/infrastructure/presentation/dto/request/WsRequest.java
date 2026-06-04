package com.letraaletra.api.shared.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
public interface WsRequest {}
