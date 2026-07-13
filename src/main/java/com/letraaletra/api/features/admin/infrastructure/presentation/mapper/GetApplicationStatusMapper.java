package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.GetApplicationStatusInput;
import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetApplicationStatusResponse;

import java.util.UUID;

public class GetApplicationStatusMapper {
    public static GetApplicationStatusInput toInput(UUID auth) {
        return new GetApplicationStatusInput(
                auth
        );
    }

    public static GetApplicationStatusResponse toResponse(GetApplicationStatusOutput output) {
        return new GetApplicationStatusResponse(
                output.users(),
                output.usersOnline(),
                output.games()
        );
    }
}
