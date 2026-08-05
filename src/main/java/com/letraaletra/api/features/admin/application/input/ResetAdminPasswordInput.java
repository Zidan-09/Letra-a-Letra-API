package com.letraaletra.api.features.admin.application.input;

public record ResetAdminPasswordInput(
            String email,
            String newPassword,
            String token
) {
}
