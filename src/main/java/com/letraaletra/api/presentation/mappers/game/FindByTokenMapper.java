package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.presentation.dto.response.http.FindByTokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindByTokenMapper {

    @Autowired
    private GameDTOMapper gameDTOMapper;

    public FindByTokenInput toCommand(String token) {
        return new FindByTokenInput(token);
    }

    public FindByTokenResponseDTO toResponseDTO(FindByTokenOutput output) {
        return new FindByTokenResponseDTO(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
