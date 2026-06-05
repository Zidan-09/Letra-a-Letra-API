package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.presentation.dto.response.http.FindByCodeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FindByCodeMapper {

    public static FindByCodeInput toInput(String code) {
        return new FindByCodeInput(code);
    }

    public static FindByCodeResponseDTO toResponseDTO(FindByCodeOutput output) {
        return new FindByCodeResponseDTO(
                output.token()
        );
    }
}
