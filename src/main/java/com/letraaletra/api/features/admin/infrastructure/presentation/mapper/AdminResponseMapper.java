package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin.AdminResponse;

public class AdminResponseMapper {
    public static AdminResponse toResponse(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail()
        );
    }
}
