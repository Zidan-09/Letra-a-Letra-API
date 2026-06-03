package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.features.user.application.input.SetAvatarInput;
import com.letraaletra.api.features.user.application.output.SetAvatarOutput;
import com.letraaletra.api.presentation.dto.request.user.SetAvatarRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.SetAvatarResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SetAvatarMapper {
    public SetAvatarInput toCommand(SetAvatarRequestDTO request, String userId) {
        return new SetAvatarInput(
                userId,
                request.avatar()
        );
    }

    public SetAvatarResponseDTO toResponseDTO(SetAvatarOutput output) {
        return new SetAvatarResponseDTO(
                output.avatar()
        );
    }
}
