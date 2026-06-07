package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.GoogleAuthRequest;

public class GoogleAuthMapper {
    public static AuthInput toInput(GoogleAuthRequest request) {
        return new AuthInput(
                request.token()
        );
    }
}
