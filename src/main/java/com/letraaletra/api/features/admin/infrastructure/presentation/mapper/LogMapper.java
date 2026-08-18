package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.LogWsResponse;

public class LogMapper {
    public static LogWsResponse toResponse(String log) {
        return new LogWsResponse(
                log
        );
    }
}
