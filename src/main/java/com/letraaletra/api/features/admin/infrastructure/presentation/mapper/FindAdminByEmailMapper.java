package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.FindAdminByEmailInput;
import com.letraaletra.api.features.admin.application.output.FindAdminByEmailOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.FindAdminByEmailResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class FindAdminByEmailMapper {
    public static FindAdminByEmailInput toInput(AuthenticatedUser principal, String email) {
        return new FindAdminByEmailInput(
                principal,
                email
        );
    }

    public static FindAdminByEmailResponse toResponse(FindAdminByEmailOutput output) {
        return new FindAdminByEmailResponse(
                AdminResponseMapper.toResponse(output.admin())
        );
    }
}
