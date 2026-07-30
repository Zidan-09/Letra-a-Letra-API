package com.letraaletra.api.features.admin.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.admin.domain.permission.Permission;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateAdminRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotNull
        List<Permission> permissions
) {
}
