package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.user.SetAvatarRequestDTO;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.user.SetAvatarResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SetAvatarMapper {
    public ChangeCosmeticInput toCommand(SetAvatarRequestDTO request, String userId) {
        return new ChangeCosmeticInput(
                userId,
                request.avatar()
        );
    }

    public SetAvatarResponseDTO toResponseDTO(ChangeCosmeticOutput output) {
        return new SetAvatarResponseDTO(
                output.avatar()
        );
    }
}
