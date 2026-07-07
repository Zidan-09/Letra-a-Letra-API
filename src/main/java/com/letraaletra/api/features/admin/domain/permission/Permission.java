package com.letraaletra.api.features.admin.domain.permission;

import java.time.LocalDateTime;
import java.util.UUID;

public record Permission(
        UUID permissionId,
        String key,
        LocalDateTime createdAt
) {
}
