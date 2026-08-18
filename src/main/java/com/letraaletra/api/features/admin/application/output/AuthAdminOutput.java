package com.letraaletra.api.features.admin.application.output;

import java.util.UUID;

public record AuthAdminOutput(
        UUID id,
        String token
) {
}
