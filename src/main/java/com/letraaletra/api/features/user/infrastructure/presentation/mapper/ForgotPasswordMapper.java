package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.ForgotPasswordInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ForgotPasswordRequest;

public class ForgotPasswordMapper {
    public static ForgotPasswordInput toInput(ForgotPasswordRequest request) {
        return new ForgotPasswordInput(
                request.email()
        );
    }
}
