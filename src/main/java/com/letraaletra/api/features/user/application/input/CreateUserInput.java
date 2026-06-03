package com.letraaletra.api.features.user.application.input;

public record CreateUserInput(
        String email,
        String password
) {
}
