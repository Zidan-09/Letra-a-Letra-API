package com.letraaletra.api.application.command.user;

public record CreateUserCommand(
        String nickname,
        String email,
        String password
) {
}
