package com.letraaletra.api.features.user.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserResponse;

public record GetMyProfileResponse(
        UserResponse user
) {
}
