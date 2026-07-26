package com.letraaletra.api.features.admin.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record RegisterAdminInput(
        AuthenticatedUser principal,
        String name,
        String email,
        String password
) {
}
