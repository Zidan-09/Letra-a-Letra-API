package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin.AdminDTO;

public class AdminDTOMapper {
    public static AdminDTO toDto(Admin admin) {
        return new AdminDTO(
                admin.getId(),
                admin.getName(),
                admin.getEmail()
        );
    }
}
