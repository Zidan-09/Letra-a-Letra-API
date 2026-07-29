package com.letraaletra.api.features.admin.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record FindAdminByEmailInput(
        AuthenticatedUser principal,
        String email
) {
}
