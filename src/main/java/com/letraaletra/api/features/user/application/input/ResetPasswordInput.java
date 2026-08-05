package com.letraaletra.api.features.user.application.input;

public record ResetPasswordInput(
        String newPassword,
        String code
) {
}
