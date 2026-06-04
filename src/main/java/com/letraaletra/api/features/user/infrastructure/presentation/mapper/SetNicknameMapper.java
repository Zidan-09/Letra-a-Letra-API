package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.UpdateNicknameInput;
import com.letraaletra.api.features.user.application.output.UpdateNicknameOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.UpdateNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.UpdateNicknameResponse;
import org.springframework.stereotype.Component;

@Component
public class SetNicknameMapper {
    public static UpdateNicknameInput toInput(UpdateNicknameRequest request, String userId) {
        return new UpdateNicknameInput(
                userId,
                request.nickname()
        );
    }

    public static UpdateNicknameResponse toResponse(UpdateNicknameOutput output) {
        return new UpdateNicknameResponse(output.nickname());
    }
}
