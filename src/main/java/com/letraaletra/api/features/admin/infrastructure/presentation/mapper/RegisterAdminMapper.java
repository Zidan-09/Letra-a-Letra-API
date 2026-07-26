package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.RegisterAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.RegisterAdminResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class RegisterAdminMapper {
    public static RegisterAdminInput toInput(AuthenticatedUser principal, RegisterAdminRequest request) {
        return new RegisterAdminInput(
                principal,
                request.name(),
                request.email(),
                request.password()
        );
    }

    public static RegisterAdminResponse toResponse(RegisterAdminOutput output) {
        return new RegisterAdminResponse(
                output.admin().getName(),
                output.admin().getEmail()
        );
    }
}
