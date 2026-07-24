package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.application.output.GetPublicGamesOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GetPublicGamesMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "game")
@Tag(name = "Game", description = "Rotas relacionadas a funcionalidade de salas")
public class GetPublicGamesController {
    private final UseCase<GetPublicGamesInput, GetPublicGamesOutput> getPublicGamesUseCase;

    public GetPublicGamesController(UseCase<GetPublicGamesInput, GetPublicGamesOutput> getPublicGamesUseCase) {
        this.getPublicGamesUseCase = getPublicGamesUseCase;
    }

    @GetMapping(path = "/public")
    public ResponseEntity<SuccessResponse<PageResponse<GameDTO>>> getGames(
            Pageable pageable
    ) {
        GetPublicGamesInput input = GetPublicGamesMapper.toInput(pageable);

        GetPublicGamesOutput output = getPublicGamesUseCase.execute(input);

        PageResponse<GameDTO> dto = GetPublicGamesMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
