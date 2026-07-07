package com.letraaletra.api.features.admin.application.input;

import java.util.UUID;

public record RegisterAdminInput(
        UUID auth,
        String name,
        String email,
        String password
) {
}
