package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.ResetPasswordInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ResetPasswordRequest;

public class ResetPasswordMapper {
    public static ResetPasswordInput toInput(ResetPasswordRequest request) {
        return new ResetPasswordInput(
                request.email(),
                request.newPassword(),
                request.code()
        );
    }
}
