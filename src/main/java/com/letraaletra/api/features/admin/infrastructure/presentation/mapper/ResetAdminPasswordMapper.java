package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.ResetAdminPasswordInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.ResetAdminPasswordRequest;

public class ResetAdminPasswordMapper {
    public static ResetAdminPasswordInput toInput(ResetAdminPasswordRequest request) {
        return new ResetAdminPasswordInput(
                request.newPassword(),
                request.token()
        );
    }
}
