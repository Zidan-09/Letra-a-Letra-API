package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.VerifyResetTokenInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.VerifyResetTokenRequest;

public class VerifyResetTokenMapper {
    public static VerifyResetTokenInput toInput(VerifyResetTokenRequest request) {
        return new VerifyResetTokenInput(
                request.token()
        );
    }
}
