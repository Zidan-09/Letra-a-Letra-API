package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.FindByCodeMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.FindByCodeResponse;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/game")
@Tag(name = "Game", description = "Rotas relacionadas a funcionalidade de salas")
public class FindGameByCodeController {
    private final UseCase<FindByCodeInput, FindByCodeOutput> findByCodeUseCase;

    public FindGameByCodeController(UseCase<FindByCodeInput, FindByCodeOutput> findByCodeUseCase) {
        this.findByCodeUseCase = findByCodeUseCase;
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SuccessResponse<FindByCodeResponse>> getGameByCode(
            @PathVariable @NotBlank String code
    ) {
        FindByCodeInput command = FindByCodeMapper.toInput(code);

        FindByCodeOutput output = findByCodeUseCase.execute(command);

        FindByCodeResponse dto = FindByCodeMapper.toResponseDTO(output);

        return ApiResponseService.success(dto);
    }
}
