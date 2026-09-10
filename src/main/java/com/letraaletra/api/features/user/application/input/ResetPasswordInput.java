package com.letraaletra.api.features.user.application.input;

public record ResetPasswordInput(
        String email,
        String newPassword,
        String code
) {
}
