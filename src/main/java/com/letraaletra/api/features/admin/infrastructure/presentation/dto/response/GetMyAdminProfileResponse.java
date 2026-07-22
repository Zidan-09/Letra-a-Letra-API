package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.admin.AdminDTO;

public record GetMyAdminProfileResponse(
        AdminDTO admin
) {
}
