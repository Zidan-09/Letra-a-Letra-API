package com.letraaletra.api.features.user.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserProfileResponse;

public record GetMyProfileResponse(
        UserProfileResponse user
) {
}
