package com.letraaletra.api.features.user.application.input;

import java.util.UUID;

public record UpdateNicknameInput(
        UUID user,
        String nickname
) {
}
