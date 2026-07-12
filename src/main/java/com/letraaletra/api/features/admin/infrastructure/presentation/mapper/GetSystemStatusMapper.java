package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.GetSystemStatusInput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetSystemStatusResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class GetSystemStatusMapper {
    public static GetSystemStatusInput toInput(AuthenticatedUser principal) {
        return new GetSystemStatusInput(
                principal
        );
    }

    public static GetSystemStatusResponse toResponse(GetSystemStatusOutput output) {
        return new GetSystemStatusResponse(
                output.uptime(),
                output.cpu(),
                output.memory(),
                output.storage(),
                output.health()
        );
    }
}
