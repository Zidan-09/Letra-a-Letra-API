package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.features.game.application.usecase.FindByTokenGameIdUseCase;
import com.letraaletra.api.features.game.domain.GameMessages;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.FindByTokenMapper;
import com.letraaletra.api.presentation.controller.ApiResponse;
import com.letraaletra.api.presentation.dto.response.http.FindByTokenResponseDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindGameByTokenController {
    private final FindByTokenGameIdUseCase findByTokenGameIdUseCase;

    public FindGameByTokenController(FindByTokenGameIdUseCase findByTokenGameIdUseCase) {
        this.findByTokenGameIdUseCase = findByTokenGameIdUseCase;
    }

    @GetMapping("/game/{tokenGameId}")
    public ResponseEntity<SuccessResponse<FindByTokenResponseDTO>> getGameById(@Valid @PathVariable String tokenGameId) {
        FindByTokenInput command = FindByTokenMapper.toInput(tokenGameId);

        FindByTokenOutput output = findByTokenGameIdUseCase.execute(command);

        FindByTokenResponseDTO dto = FindByTokenMapper.toResponseDTO(output);

        return ApiResponse.success(dto, GameMessages.GAME_FOUND.getMessage());
    }
}
