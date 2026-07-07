package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.usecase.GetPublicGamesUseCase;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GetGamesResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GetGamesMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "game")
@Tag(name = "Game", description = "Rotas relacionadas a funcionalidade de salas")
public class GetGamesController {
    private final GetPublicGamesUseCase getPublicGamesUseCase;

    public GetGamesController(GetPublicGamesUseCase getPublicGamesUseCase) {
        this.getPublicGamesUseCase = getPublicGamesUseCase;
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<GetGamesResponse>> getGames() {
        GetGamesOutput output = getPublicGamesUseCase.execute(null);

        GetGamesResponse dto = GetGamesMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
