package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeNicknameResponse;

import java.util.UUID;

public class ChangeNicknameMapper {
    public static ChangeNicknameInput toInput(UUID auth, ChangeNicknameRequest request) {
        return new ChangeNicknameInput(
                auth,
                request.nickname()
        );
    }

    public static ChangeNicknameResponse toResponse(ChangeNicknameOutput output) {
        return new ChangeNicknameResponse(
                output.user().getNickname()
        );
    }
}
