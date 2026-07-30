package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.UpdateAdminInput;
import com.letraaletra.api.features.admin.application.output.UpdateAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.UpdateAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.UpdateAdminResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class UpdateAdminMapper {
    public static UpdateAdminInput toInput(AuthenticatedUser principal, UUID adminId, UpdateAdminRequest request) {
        return new UpdateAdminInput(
                principal,
                adminId,
                request.name(),
                request.email(),
                request.permissions()
        );
    }

    public static UpdateAdminResponse toResponse(UpdateAdminOutput output) {
        return new UpdateAdminResponse(
                AdminResponseMapper.toResponse(output.admin())
        );
    }
}
