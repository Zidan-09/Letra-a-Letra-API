package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.FindByCodeResponse;

public class FindByCodeMapper {
    public static FindByCodeInput toInput(String code) {
        return new FindByCodeInput(code);
    }

    public static FindByCodeResponse toResponseDTO(FindByCodeOutput output) {
        return new FindByCodeResponse(
                output.gameId()
        );
    }
}
