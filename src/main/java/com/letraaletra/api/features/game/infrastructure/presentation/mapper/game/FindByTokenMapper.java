package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.FindByTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class FindByTokenMapper {
    public static FindByTokenInput toInput(String token) {
        return new FindByTokenInput(token);
    }

    public static FindByTokenResponse toResponseDTO(FindByTokenOutput output) {
        return new FindByTokenResponse(
                GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
