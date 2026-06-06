package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.usecase.FindByCodeUseCase;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.FindByCodeMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.FindByCodeResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindGameByCodeController {
    private final FindByCodeUseCase findByCodeUseCase;

    public FindGameByCodeController(FindByCodeUseCase findByCodeUseCase) {
        this.findByCodeUseCase = findByCodeUseCase;
    }

    @GetMapping("/game/code/{code}")
    public ResponseEntity<SuccessResponse<FindByCodeResponse>> getGameByCode(@Valid @PathVariable String code) {
        FindByCodeInput command = FindByCodeMapper.toInput(code);

        FindByCodeOutput output = findByCodeUseCase.execute(command);

        FindByCodeResponse dto = FindByCodeMapper.toResponseDTO(output);

        return ApiResponseService.success(dto);
    }
}
