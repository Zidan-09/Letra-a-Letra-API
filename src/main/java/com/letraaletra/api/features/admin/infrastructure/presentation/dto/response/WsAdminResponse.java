package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "event"
)
public interface WsAdminResponse {}
