package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.ForgotAdminPasswordInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.ForgotAdminPasswordRequest;

public class ForgotAdminPasswordMapper {
    public static ForgotAdminPasswordInput toInput(ForgotAdminPasswordRequest request) {
        return new ForgotAdminPasswordInput(
                request.email()
        );
    }
}
