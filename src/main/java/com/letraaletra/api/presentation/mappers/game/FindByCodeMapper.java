package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.presentation.dto.response.http.FindByCodeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FindByCodeMapper {

    public FindByCodeInput toCommand(String code) {
        return new FindByCodeInput(code);
    }

    public FindByCodeResponseDTO toResponseDTO(FindByCodeOutput output) {
        return new FindByCodeResponseDTO(
                output.token()
        );
    }
}
