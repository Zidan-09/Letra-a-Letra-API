package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.SetNicknameInput;
import com.letraaletra.api.features.user.application.output.SetNicknameOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.UpdateNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SetNicknameResponse;
import org.springframework.stereotype.Component;

@Component
public class SetNicknameMapper {
    public SetNicknameInput toInput(UpdateNicknameRequest request, String userId) {
        return new SetNicknameInput(
                userId,
                request.nickname()
        );
    }

    public SetNicknameResponse toResponse(SetNicknameOutput output) {
        return new SetNicknameResponse(output.nickname());
    }
}
