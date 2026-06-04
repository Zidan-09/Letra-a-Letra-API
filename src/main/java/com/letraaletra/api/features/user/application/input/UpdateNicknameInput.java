package com.letraaletra.api.features.user.application.input;

public record UpdateNicknameInput(
        String user,
        String nickname
) {
}
