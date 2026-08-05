package com.letraaletra.api.features.admin.application.input;

public record ResetAdminPasswordInput(
            String newPassword,
            String token
) {
}
