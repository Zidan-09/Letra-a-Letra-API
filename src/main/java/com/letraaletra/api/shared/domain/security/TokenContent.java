package com.letraaletra.api.shared.domain.security;

import java.util.UUID;

public record TokenContent(
        UUID id,
        Roles role
) {
}
