package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.DeleteAdminInput;
import com.letraaletra.api.features.admin.application.output.DeleteAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.DeleteAdminResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DeleteAdminMapper {
    public static DeleteAdminInput toInput(AuthenticatedUser principal, UUID adminId) {
        return new DeleteAdminInput(
                principal,
                adminId
        );
    }

    public static DeleteAdminResponse toResponse(DeleteAdminOutput output) {
        return new DeleteAdminResponse(
                AdminResponseMapper.toResponse(output.admin())
        );
    }
}
