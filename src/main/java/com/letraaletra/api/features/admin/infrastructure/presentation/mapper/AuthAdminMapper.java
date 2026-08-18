package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.AuthAdminInput;
import com.letraaletra.api.features.admin.application.output.AuthAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.AuthAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.AuthAdminResponse;

public class AuthAdminMapper {
    public static AuthAdminInput toInput(AuthAdminRequest request) {
        return new AuthAdminInput(
                request.email(),
                request.password()
        );
    }

    public static AuthAdminResponse toResponse(AuthAdminOutput output) {
        return new AuthAdminResponse(
                output.id().toString(),
                output.token()
        );
    }
}
