package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin.AdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin.PermissionResponse;

import java.util.stream.Collectors;

public class AdminResponseMapper {
    public static AdminResponse toResponse(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPermissions()
                        .getAll()
                        .stream()
                        .map(permission -> new PermissionResponse(
                                permission.key(),
                                permission.actions()
                        ))
                        .collect(Collectors.toSet())
        );
    }
}
