package com.letraaletra.api.features.user.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserDTO;

public record GetMyProfileResponse(
        UserDTO user
) {
}
