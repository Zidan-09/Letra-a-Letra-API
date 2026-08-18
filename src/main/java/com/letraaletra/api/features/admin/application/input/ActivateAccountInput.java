package com.letraaletra.api.features.admin.application.input;

public record ActivateAccountInput(
        String token,
        String password
) {
}
