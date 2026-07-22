package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.GetMyProfileInput;
import com.letraaletra.api.features.user.application.output.GetMyProfileOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetMyProfileResponse;

import java.util.UUID;

public class GetMyProfileMapper {
    public static GetMyProfileInput toInput(UUID userId) {
        return new GetMyProfileInput(
                userId
        );
    }

    public static GetMyProfileResponse toResponse(GetMyProfileOutput output) {
        return new GetMyProfileResponse(
                UserDTOMapper.toDto(output.user())
        );
    }
}
