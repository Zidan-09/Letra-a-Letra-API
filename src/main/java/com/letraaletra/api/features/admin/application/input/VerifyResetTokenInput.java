package com.letraaletra.api.features.admin.application.input;

public record VerifyResetTokenInput(
        String email,
        String token
) {
}
