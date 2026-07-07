package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.RegisterAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.RegisterAdminResponse;

import java.util.UUID;

public class RegisterAdminMapper {
    public static RegisterAdminInput toInput(UUID auth, RegisterAdminRequest request) {
        return new RegisterAdminInput(
                auth,
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
