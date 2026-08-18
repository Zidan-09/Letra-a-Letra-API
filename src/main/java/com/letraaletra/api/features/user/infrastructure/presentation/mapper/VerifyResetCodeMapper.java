package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.VerifyResetCodeRequest;

public class VerifyResetCodeMapper {
    public static VerifyResetCodeInput toInput(VerifyResetCodeRequest request) {
        return new VerifyResetCodeInput(
                request.code()
        );
    }
}
