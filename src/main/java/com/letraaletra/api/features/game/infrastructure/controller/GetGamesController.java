package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.usecase.GetPublicGamesUseCase;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GetGamesResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GetGamesMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetGamesController {
    private final GetPublicGamesUseCase getPublicGamesUseCase;

    public GetGamesController(GetPublicGamesUseCase getPublicGamesUseCase) {
        this.getPublicGamesUseCase = getPublicGamesUseCase;
    }

    @GetMapping(path = "/game")
    public ResponseEntity<SuccessResponse<GetGamesResponse>> getGames() {
        GetGamesOutput output = getPublicGamesUseCase.execute(null);

        GetGamesResponse dto = GetGamesMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
