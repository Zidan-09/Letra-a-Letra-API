package com.letraaletra.api.shared.domain;

import java.util.UUID;

public record AuthenticatedUser(
        UUID auth,
        String name,
        boolean isAdmin
) {
}
