package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;

@JsonTypeName("METRICS")
public record MetricsWsResponse(
        GetSystemStatusOutput system,
        GetApplicationStatusOutput application
) implements WsAdminResponse {
}
