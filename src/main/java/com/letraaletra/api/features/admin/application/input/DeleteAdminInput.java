package com.letraaletra.api.features.admin.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record DeleteAdminInput(
        AuthenticatedUser principal,
        UUID adminId
) {
}
