package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.MetricsWsResponse;

public class MetricsMapper {
    public static MetricsWsResponse toResponse(
            GetSystemStatusOutput systemStatusOutput,
            GetApplicationStatusOutput applicationStatusOutput
    ) {
        return new MetricsWsResponse(
                systemStatusOutput,
                applicationStatusOutput
        );
    }
}
