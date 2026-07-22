package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.GetMyAdminProfileInput;
import com.letraaletra.api.features.admin.application.output.GetMyAdminProfileOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.GetMyAdminProfileResponse;

import java.util.UUID;

public class GetMyAdminProfileMapper {
    public static GetMyAdminProfileInput toInput(UUID adminId) {
        return new GetMyAdminProfileInput(
                adminId
        );
    }

    public static GetMyAdminProfileResponse toResponse(GetMyAdminProfileOutput output) {
        return new GetMyAdminProfileResponse(
                AdminDTOMapper.toDto(output.admin())
        );
    }
}
