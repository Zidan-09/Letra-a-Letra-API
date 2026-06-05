package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.presentation.dto.response.http.FindByTokenResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FindByTokenMapper {
    public static FindByTokenInput toInput(String token) {
        return new FindByTokenInput(token);
    }

    public static FindByTokenResponseDTO toResponseDTO(FindByTokenOutput output) {
        return new FindByTokenResponseDTO(
                GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
